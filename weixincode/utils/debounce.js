function debounce(fn, delay = 300) {
  let timer = null
  
  return function(...args) {
    if (timer) {
      clearTimeout(timer)
    }
    
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

function throttle(fn, delay = 300) {
  let lastTime = 0
  
  return function(...args) {
    const now = Date.now()
    
    if (now - lastTime >= delay) {
      lastTime = now
      fn.apply(this, args)
    }
  }
}

function immediate(fn, delay = 300) {
  let timer = null
  
  return function(...args) {
    if (timer) {
      clearTimeout(timer)
    } else {
      fn.apply(this, args)
    }
    
    timer = setTimeout(() => {
      timer = null
    }, delay)
  }
}

function raf(fn) {
  let pending = false
  
  return function(...args) {
    if (!pending) {
      pending = true
      
      wx.nextTick(() => {
        fn.apply(this, args)
        pending = false
      })
    }
  }
}

module.exports = {
  debounce,
  throttle,
  immediate,
  raf
}
