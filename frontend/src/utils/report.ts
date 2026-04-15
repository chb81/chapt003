export interface ReportConfig {
  title: string
  description?: string
  logo?: string
  generatedBy?: string
  dateRange?: {
    start: Date
    end: Date
  }
}

export interface ReportSection {
  title: string
  type: 'table' | 'chart' | 'text' | 'list'
  data: any
  config?: any
}

export interface ReportData {
  config: ReportConfig
  sections: ReportSection[]
}

export interface ReportTemplate {
  id: string
  name: string
  description: string
  sections: Omit<ReportSection, 'data'>[]
  config: Partial<ReportConfig>
}

export interface ExportOptions {
  format: 'pdf' | 'excel' | 'word'
  fileName?: string
  landscape?: boolean
  pageSize?: 'a4' | 'a3' | 'letter'
  includeCharts?: boolean
  includeHeaders?: boolean
  includeFooters?: boolean
}

export interface ScheduleReport {
  id: string
  templateId: string
  name: string
  schedule: 'daily' | 'weekly' | 'monthly' | 'quarterly'
  recipients: string[]
  nextRunDate: Date
  enabled: boolean
}

class ReportGenerator {
  private templates: Map<string, ReportTemplate> = new Map()
  private scheduledReports: Map<string, ScheduleReport> = new Map()

  constructor() {
    this.initializeDefaultTemplates()
  }

  private initializeDefaultTemplates() {
    const defaultTemplates: ReportTemplate[] = [
      {
        id: 'volunteer-activity',
        name: '志愿者活动报表',
        description: '志愿者活动统计报表',
        sections: [
          { title: '活动概况', type: 'text', config: {} },
          { title: '活动列表', type: 'table', config: {} },
          { title: '活动趋势', type: 'chart', config: { chartType: 'line' } }
        ],
        config: {
          title: '志愿者活动报表',
          description: '志愿者活动统计与趋势分析'
        }
      },
      {
        id: 'volunteer-hours',
        name: '服务时长报表',
        description: '志愿者服务时长统计',
        sections: [
          { title: '时长统计', type: 'table', config: {} },
          { title: '时长分布', type: 'chart', config: { chartType: 'bar' } }
        ],
        config: {
          title: '服务时长报表',
          description: '志愿者服务时长统计分析'
        }
      },
      {
        id: 'volunteer-performance',
        name: '志愿者绩效报表',
        description: '志愿者绩效评估报表',
        sections: [
          { title: '绩效指标', type: 'table', config: {} },
          { title: '绩效趋势', type: 'chart', config: { chartType: 'radar' } }
        ],
        config: {
          title: '志愿者绩效报表',
          description: '志愿者绩效评估与排名'
        }
      }
    ]

    defaultTemplates.forEach(template => {
      this.templates.set(template.id, template)
    })
  }

  getTemplate(id: string): ReportTemplate | undefined {
    return this.templates.get(id)
  }

  getAllTemplates(): ReportTemplate[] {
    return Array.from(this.templates.values())
  }

  createTemplate(template: Omit<ReportTemplate, 'id'>): ReportTemplate {
    const id = `template-${Date.now()}`
    const newTemplate: ReportTemplate = {
      ...template,
      id
    }
    this.templates.set(id, newTemplate)
    return newTemplate
  }

  updateTemplate(id: string, updates: Partial<ReportTemplate>): ReportTemplate | null {
    const template = this.templates.get(id)
    if (!template) return null

    const updated = { ...template, ...updates }
    this.templates.set(id, updated)
    return updated
  }

  deleteTemplate(id: string): boolean {
    return this.templates.delete(id)
  }

  generateReport(templateId: string, data: Record<string, any>): ReportData {
    const template = this.getTemplate(templateId)
    if (!template) {
      throw new Error(`Template not found: ${templateId}`)
    }

    const sections: ReportSection[] = template.sections.map(section => {
      const sectionData = data[section.title] || []
      return {
        title: section.title,
        type: section.type,
        data: sectionData,
        config: section.config
      }
    })

    return {
      config: {
        ...template.config,
        generatedBy: '系统管理员',
        dateRange: data.dateRange
      },
      sections
    }
  }

  async exportToPDF(report: ReportData, options: ExportOptions = { format: 'pdf' }): Promise<void> {
    const { jsPDF } = await import('jspdf')
    const doc = new jsPDF({
      orientation: options.landscape ? 'landscape' : 'portrait',
      unit: 'mm',
      format: options.pageSize || 'a4'
    })

    let yPosition = 20
    const pageWidth = doc.internal.pageSize.getWidth()
    const pageHeight = doc.internal.pageSize.getHeight()
    const margin = 20

    doc.setFontSize(20)
    doc.text(report.config.title, pageWidth / 2, yPosition, { align: 'center' })
    yPosition += 15

    if (report.config.description) {
      doc.setFontSize(12)
      doc.setTextColor(100, 100, 100)
      doc.text(report.config.description, pageWidth / 2, yPosition, { align: 'center' })
      yPosition += 15
    }

    doc.setTextColor(0, 0, 0)

    if (report.config.dateRange) {
      doc.setFontSize(10)
      doc.text(
        `报表周期: ${this.formatDate(report.config.dateRange.start)} - ${this.formatDate(report.config.dateRange.end)}`,
        margin,
        yPosition
      )
      yPosition += 10
    }

    doc.setFontSize(10)
    doc.text(`生成时间: ${this.formatDate(new Date())}`, margin, yPosition)
    yPosition += 5
    doc.text(`生成人: ${report.config.generatedBy || '系统'}`, margin, yPosition)
    yPosition += 15

    for (const section of report.sections) {
      if (yPosition > pageHeight - 50) {
        doc.addPage()
        yPosition = 20
      }

      doc.setFontSize(14)
      doc.setFont('helvetica', 'bold')
      doc.text(section.title, margin, yPosition)
      yPosition += 10

      doc.setFont('helvetica', 'normal')

      switch (section.type) {
        case 'table':
          yPosition = this.drawTable(doc, section.data, yPosition, margin, pageWidth)
          break
        case 'list':
          yPosition = this.drawList(doc, section.data, yPosition, margin, pageWidth)
          break
        case 'text':
          yPosition = this.drawText(doc, section.data, yPosition, margin, pageWidth)
          break
      }

      yPosition += 10
    }

    doc.save(options.fileName || `report-${Date.now()}.pdf`)
  }

  private drawTable(doc: any, data: any[], startY: number, margin: number, pageWidth: number): number {
    if (!data || data.length === 0) {
      doc.setFontSize(10)
      doc.text('无数据', margin, startY)
      return startY + 10
    }

    const columns = Object.keys(data[0])
    const cellWidth = (pageWidth - 2 * margin) / columns.length
    let y = startY

    doc.setFontSize(9)
    doc.setFont('helvetica', 'bold')

    columns.forEach((col, index) => {
      const x = margin + index * cellWidth
      doc.text(col, x, y)
    })

    y += 8
    doc.setDrawColor(200, 200, 200)
    doc.line(margin, y - 2, pageWidth - margin, y - 2)

    doc.setFont('helvetica', 'normal')

    data.forEach((row, rowIndex) => {
      if (y > doc.internal.pageSize.getHeight() - 20) {
        doc.addPage()
        y = 20
      }

      columns.forEach((col, colIndex) => {
        const x = margin + colIndex * cellWidth
        let text = String(row[col] || '')
        if (text.length > 15) {
          text = text.substring(0, 15) + '...'
        }
        doc.text(text, x, y)
      })

      y += 7
    })

    return y + 5
  }

  private drawList(doc: any, data: any[], startY: number, margin: number, pageWidth: number): number {
    if (!data || data.length === 0) {
      doc.setFontSize(10)
      doc.text('无数据', margin, startY)
      return startY + 10
    }

    let y = startY
    doc.setFontSize(10)

    data.forEach((item, index) => {
      if (y > doc.internal.pageSize.getHeight() - 20) {
        doc.addPage()
        y = 20
      }

      const text = `${index + 1}. ${JSON.stringify(item)}`
      const lines = doc.splitTextToSize(text, pageWidth - 2 * margin)
      lines.forEach((line: string) => {
        doc.text(line, margin, y)
        y += 6
      })
      y += 3
    })

    return y
  }

  private drawText(doc: any, text: string, startY: number, margin: number, pageWidth: number): number {
    doc.setFontSize(10)
    const lines = doc.splitTextToSize(text || '', pageWidth - 2 * margin)
    let y = startY

    lines.forEach((line: string) => {
      if (y > doc.internal.pageSize.getHeight() - 20) {
        doc.addPage()
        y = 20
      }
      doc.text(line, margin, y)
      y += 6
    })

    return y
  }

  async exportToExcel(report: ReportData, options: ExportOptions = { format: 'excel' }): Promise<void> {
    const XLSX = await import('xlsx')

    const workbook = XLSX.utils.book_new()

    for (const section of report.sections) {
      if (section.type === 'table' && Array.isArray(section.data)) {
        const worksheet = XLSX.utils.json_to_sheet(section.data)
        XLSX.utils.book_append_sheet(workbook, worksheet, section.title.substring(0, 31))
      }
    }

    XLSX.writeFile(workbook, options.fileName || `report-${Date.now()}.xlsx`)
  }

  scheduleReport(schedule: Omit<ScheduleReport, 'id' | 'nextRunDate'>): ScheduleReport {
    const id = `schedule-${Date.now()}`
    const nextRunDate = this.calculateNextRunDate(schedule.schedule)

    const newSchedule: ScheduleReport = {
      ...schedule,
      id,
      nextRunDate
    }

    this.scheduledReports.set(id, newSchedule)
    return newSchedule
  }

  getScheduledReports(): ScheduleReport[] {
    return Array.from(this.scheduledReports.values())
  }

  updateSchedule(id: string, updates: Partial<ScheduleReport>): ScheduleReport | null {
    const schedule = this.scheduledReports.get(id)
    if (!schedule) return null

    const updated = { ...schedule, ...updates }
    if (updates.schedule) {
      updated.nextRunDate = this.calculateNextRunDate(updates.schedule)
    }
    this.scheduledReports.set(id, updated)
    return updated
  }

  deleteSchedule(id: string): boolean {
    return this.scheduledReports.delete(id)
  }

  private calculateNextRunDate(schedule: 'daily' | 'weekly' | 'monthly' | 'quarterly'): Date {
    const now = new Date()
    const next = new Date(now)

    switch (schedule) {
      case 'daily':
        next.setDate(now.getDate() + 1)
        next.setHours(9, 0, 0, 0)
        break
      case 'weekly':
        next.setDate(now.getDate() + 7)
        next.setHours(9, 0, 0, 0)
        break
      case 'monthly':
        next.setMonth(now.getMonth() + 1)
        next.setDate(1)
        next.setHours(9, 0, 0, 0)
        break
      case 'quarterly':
        next.setMonth(now.getMonth() + 3)
        next.setDate(1)
        next.setHours(9, 0, 0, 0)
        break
    }

    return next
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }

  getReportStatistics(): {
    totalTemplates: number
    totalScheduledReports: number
    activeScheduledReports: number
  } {
    return {
      totalTemplates: this.templates.size,
      totalScheduledReports: this.scheduledReports.size,
      activeScheduledReports: Array.from(this.scheduledReports.values()).filter(r => r.enabled).length
    }
  }
}

export const reportGenerator = new ReportGenerator()
export default ReportGenerator
