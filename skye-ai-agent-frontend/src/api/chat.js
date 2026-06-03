import request from './request'

/**
 * 解析 SSE 串流並逐段回調內容。
 * 瀏覽器端 SSE 需使用 fetch，axios 無法處理 ReadableStream。
 */
async function streamSseGet(path, params, { onMessage, onError, signal }) {
  const url = `${request.defaults.baseURL}${path}?${new URLSearchParams(params)}`

  try {
    const response = await fetch(url, { signal })

    if (!response.ok) {
      throw new Error(`請求失敗：${response.status} ${response.statusText}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      buffer = parseSseBuffer(buffer, onMessage)
    }

    if (buffer.trim()) {
      parseSseBuffer(buffer + '\n', onMessage)
    }
  } catch (error) {
    if (error.name !== 'AbortError') {
      onError?.(error)
    }
    throw error
  }
}

function parseSseBuffer(buffer, onMessage) {
  const lines = buffer.split('\n')
  const remaining = lines.pop() ?? ''

  for (const line of lines) {
    const trimmed = line.trimEnd()

    if (!trimmed || trimmed.startsWith(':')) continue

    if (trimmed.startsWith('data:')) {
      const data = trimmed.slice(5).trimStart()
      if (data && data !== '[DONE]') {
        onMessage(data)
      }
      continue
    }

    if (!trimmed.startsWith('event:') && !trimmed.startsWith('id:')) {
      onMessage(trimmed)
    }
  }

  return remaining
}

/** AI 健康問答 SSE */
export function chatWithHealthApp(message, chatId, callbacks) {
  return streamSseGet(
    '/ai/health_app/chat/sse',
    { message, chatId },
    callbacks,
  )
}

/** AI 超級智能體 SSE */
export function chatWithManus(message, callbacks) {
  return streamSseGet('/ai/manus/chat', { message }, callbacks)
}
