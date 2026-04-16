function validateEmail(email) {
  const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return reg.test(email)
}

function validateMobile(mobile) {
  const reg = /^1[3-9]\d{9}$/
  return reg.test(mobile)
}

function validatePassword(password) {
  if (password.length < 8 || password.length > 20) {
    return { valid: false, message: '密码长度必须在8-20位之间' }
  }
  
  const hasLetter = /[a-zA-Z]/.test(password)
  const hasNumber = /[0-9]/.test(password)
  
  if (!hasLetter || !hasNumber) {
    return { valid: false, message: '密码必须包含字母和数字' }
  }
  
  return { valid: true, message: '' }
}

function validateConfirmPassword(password, confirmPassword) {
  if (password !== confirmPassword) {
    return { valid: false, message: '两次输入的密码不一致' }
  }
  return { valid: true, message: '' }
}

module.exports = {
  validateEmail,
  validateMobile,
  validatePassword,
  validateConfirmPassword
}
