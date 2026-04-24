<template>
  <div class="help-docs">
    <el-card>
      <template #header><span>帮助文档</span></template>
      <el-input v-model="keyword" placeholder="搜索文档" clearable @keyup.enter="searchDocs" style="margin-bottom: 15px; width: 300px">
        <template #append><el-button @click="searchDocs">搜索</el-button></template>
      </el-input>
      <el-table :data="documents" stripe v-loading="loading">
        <el-table-column prop="title" label="文档标题" min-width="250">
          <template #default="{ row }">
            <el-link type="primary" @click="viewDoc(row)">{{ row.title }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="viewCount" label="浏览量" width="80" />
>
      </el-table>
    </el-card>

    <el-dialog v-model="showDoc" :title="currentDoc.title" width="700px">
      <div v-html="currentDoc.content" class="doc-content" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getHelpDocuments, searchHelpDocuments, getHelpDocumentDetail } from '@/api/help'

const loading = ref(false)
const keyword = ref('')
const documents = ref<any[]>([])
const showDoc = ref(false)
const currentDoc = ref<any>({})

const loadDocs = async () => {
  loading.value = true
  try {
    const res = await getHelpDocuments()
    documents.value = res.data || []
  } catch (e) { documents.value = [] }
  finally { loading.value = false }
}

const searchDocs = async () => {
  if (!keyword.value.trim()) { loadDocs(); return }
  loading.value = true
  try {
    const res = await searchHelpDocuments(keyword.value)
    documents.value = res.data || []
  } catch (e) { documents.value = [] }
  finally { loading.value = false }
}

const viewDoc = async (doc: any) => {
  try {
    const res = await getHelpDocumentDetail(doc.id)
    currentDoc.value = res.data || doc
  } catch (e) {
    currentDoc.value = doc
  }
  showDoc.value = true
}

onMounted(() => { loadDocs() })
</script>

<style scoped>
.doc-content {
  line-height: 1.8;
  padding: 10px;
}
</style>