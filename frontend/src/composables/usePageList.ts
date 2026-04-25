import { ref, reactive } from 'vue'

interface PageListOptions {
  defaultPageSize?: number
  apiFn: (params: any) => Promise<any>
  dataExtractor?: (res: any) => { content: any[]; totalElements: number }
}

export function usePageList(options: PageListOptions) {
  const loading = ref(false)
  const dataList = ref<any[]>([])
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(options.defaultPageSize || 20)
  const filters = reactive<Record<string, any>>({})

  const loadData = async () => {
    loading.value = true
    try {
      const params: any = {
        page: currentPage.value - 1,
        size: pageSize.value,
        ...filters
      }

      Object.keys(params).forEach(key => {
        if (params[key] === undefined || params[key] === '' || params[key] === null) {
          delete params[key]
        }
      })

      const res = await options.apiFn(params)

      if (options.dataExtractor) {
        const extracted = options.dataExtractor(res)
        dataList.value = extracted.content
        total.value = extracted.totalElements
      } else {
        const data = res.data || res
        dataList.value = data.content || []
        total.value = data.totalElements || 0
      }
    } catch (error: any) {
      dataList.value = []
      total.value = 0
      throw error
    } finally {
      loading.value = false
    }
  }

  const handleSearch = () => {
    currentPage.value = 1
    return loadData()
  }

  const handleReset = () => {
    Object.keys(filters).forEach(key => {
      filters[key] = undefined
    })
    currentPage.value = 1
    return loadData()
  }

  const handlePageChange = () => {
    return loadData()
  }

  const handleSizeChange = () => {
    currentPage.value = 1
    return loadData()
  }

  return {
    loading,
    dataList,
    total,
    currentPage,
    pageSize,
    filters,
    loadData,
    handleSearch,
    handleReset,
    handlePageChange,
    handleSizeChange
  }
}
