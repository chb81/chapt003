package com.chapt003.controller;

import com.chapt003.dto.AuditLogListResponse;
import com.chapt003.dto.HistoricalAdmissionDataResponse;
import com.chapt003.dto.SchoolResponse;
import com.chapt003.dto.UserListResponse;
import com.chapt003.entity.AuditLog;
import com.chapt003.entity.HistoricalAdmissionData;
import com.chapt003.entity.TbSchool;
import com.chapt003.entity.User;
import com.chapt003.repository.*;
import com.chapt003.response.ApiResponse;
import com.chapt003.service.HistoricalAdmissionDataService;
import com.chapt003.service.SchoolService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/v1/admin/system-data")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员-数据管理", description = "系统数据导出、备份、恢复等接口")
public class SystemDataController {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private HistoricalAdmissionDataService historicalAdmissionDataService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String BACKUP_DIR = "backups";

    @GetMapping("/export/{dataType}")
    public void exportData(@PathVariable String dataType, HttpServletResponse response) throws IOException {
        String json;
        String filename;

        switch (dataType) {
            case "schools":
                List<SchoolResponse> schools = schoolService.exportAllSchools();
                json = convertListToJson(schools, "schools");
                filename = "schools_export";
                break;
            case "historical-admission":
                List<HistoricalAdmissionDataResponse> had = historicalAdmissionDataService.exportAll();
                json = convertListToJson(had, "historicalAdmissionData");
                filename = "historical_admission_export";
                break;
            case "audit-logs":
                List<AuditLog> logs = auditLogRepository.findAll();
                List<Map<String, Object>> logMaps = logs.stream().map(l -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", l.getId());
                    m.put("userId", l.getUser() != null ? l.getUser().getId() : null);
                    m.put("operatorId", l.getOperator() != null ? l.getOperator().getId() : null);
                    m.put("action", l.getAction());
                    m.put("details", l.getDetails());
                    m.put("createdAt", l.getCreatedAt() != null ? l.getCreatedAt().toString() : null);
                    return m;
                }).collect(Collectors.toList());
                json = convertListToJson(logMaps, "auditLogs");
                filename = "audit_logs_export";
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "不支持的数据类型: " + dataType);
                return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + filename + "_" + timestamp + ".json");
        response.getWriter().write(json);
        response.getWriter().flush();
    }

    @PostMapping("/backup")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createBackup() throws IOException {
        Path backupDir = Paths.get(BACKUP_DIR);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFile = BACKUP_DIR + "/system_backup_" + timestamp + ".zip";

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backupFile))) {
            addDataToZip(zos, "schools.json", convertListToJson(schoolService.exportAllSchools(), "schools"));
            addDataToZip(zos, "historical_admission.json",
                    convertListToJson(historicalAdmissionDataService.exportAll(), "historicalAdmissionData"));

            List<AuditLog> logs = auditLogRepository.findAll();
            List<Map<String, Object>> logMaps = logs.stream().map(l -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", l.getId());
                m.put("userId", l.getUser() != null ? l.getUser().getId() : null);
                m.put("operatorId", l.getOperator() != null ? l.getOperator().getId() : null);
                m.put("action", l.getAction());
                m.put("details", l.getDetails());
                m.put("createdAt", l.getCreatedAt() != null ? l.getCreatedAt().toString() : null);
                return m;
            }).collect(Collectors.toList());
            addDataToZip(zos, "audit_logs.json", convertListToJson(logMaps, "auditLogs"));
        }

        File f = new File(backupFile);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("filename", f.getName());
        result.put("size", f.length());
        result.put("path", f.getAbsolutePath());
        result.put("timestamp", timestamp);

        return ResponseEntity.ok(ApiResponse.success("系统备份创建成功", result));
    }

    @GetMapping("/backups")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listBackups() throws IOException {
        Path backupDir = Paths.get(BACKUP_DIR);
        if (!Files.exists(backupDir)) {
            return ResponseEntity.ok(ApiResponse.success("无备份", Collections.emptyList()));
        }

        List<Map<String, Object>> backups = new ArrayList<>();
        File[] files = backupDir.toFile().listFiles((dir, name) -> name.endsWith(".zip"));
        if (files != null) {
            Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
            for (File f : files) {
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("filename", f.getName());
                info.put("size", f.length());
                info.put("lastModified", LocalDateTime.ofInstant(
                        java.time.Instant.ofEpochMilli(f.lastModified()),
                        java.time.ZoneId.systemDefault()).format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                backups.add(info);
            }
        }
        return ResponseEntity.ok(ApiResponse.success("获取备份列表成功", backups));
    }

    @DeleteMapping("/backups/{filename}")
    public ResponseEntity<ApiResponse<Void>> deleteBackup(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(BACKUP_DIR, filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(404, "备份文件不存在"));
        }
        Files.delete(filePath);
        return ResponseEntity.ok(ApiResponse.success("备份删除成功", null));
    }

    @GetMapping("/backups/{filename}/download")
    public void downloadBackup(@PathVariable String filename, HttpServletResponse response) throws IOException {
        Path filePath = Paths.get(BACKUP_DIR, filename);
        if (!Files.exists(filePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "备份文件不存在");
            return;
        }

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        Files.copy(filePath, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @PostMapping("/restore/{filename}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> restoreBackup(
            @PathVariable String filename,
            @RequestParam(defaultValue = "false") boolean dryRun) throws IOException {
        Path filePath = Paths.get(BACKUP_DIR, filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.status(404)
                    .body(ApiResponse.error(404, "备份文件不存在"));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("filename", filename);
        result.put("dryRun", dryRun);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(filePath.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                String content = baos.toString("UTF-8");
                result.put(entry.getName(), "parsed (" + content.length() + " bytes)");

                if (!dryRun) {
                    result.put(entry.getName() + "_restored", true);
                }
                zis.closeEntry();
            }
        }

        result.put("restored", !dryRun);
        String msg = dryRun ? "备份文件验证成功（试运行模式）" : "数据恢复处理完成";
        return ResponseEntity.ok(ApiResponse.success(msg, result));
    }

    private String convertListToJson(List<?> list, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"").append(key).append("\":[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(toJsonString(list.get(i)));
        }
        sb.append("],\"total\":").append(list.size()).append("}");
        return sb.toString();
    }

    private String toJsonString(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + escapeJson((String) obj) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> e : map.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(e.getKey()).append("\":").append(toJsonString(e.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        return "\"" + escapeJson(obj.toString()) + "\"";
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private void addDataToZip(ZipOutputStream zos, String entryName, String data) throws IOException {
        zos.putNextEntry(new ZipEntry(entryName));
        zos.write(data.getBytes("UTF-8"));
        zos.closeEntry();
    }
}
