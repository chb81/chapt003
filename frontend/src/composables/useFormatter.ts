export function useFormatter() {
  const formatDate = (date: string | null | undefined): string => {
    if (!date) return ''
    return new Date(date).toLocaleString('zh-CN')
  }

  const formatShortDate = (date: string | null | undefined): string => {
    if (!date) return ''
    return new Date(date).toLocaleDateString('zh-CN')
  }

  const formatNumber = (value: number | null | undefined): string => {
    if (value == null) return '-'
    return value.toLocaleString('zh-CN')
  }

  const formatPercent = (value: number | null | undefined): string => {
    if (value == null) return '-'
    return value.toFixed(2) + '%'
  }

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  return {
    formatDate,
    formatShortDate,
    formatNumber,
    formatPercent,
    formatFileSize
  }
}
