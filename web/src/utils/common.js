import {JSEncrypt} from "jsencrypt";

/**
 * 时间格式化
 * @param date long
 * @param format yyyy-MM-dd yyyy-MM-dd HH:mm:ss
 * @returns {string} 指定格式
 */
export function dateToString(date, format) {
    const tmp = new Date(date);
    if (format === "yyyy-MM-dd") {
        return tmp.getFullYear() + '-' + (tmp.getMonth() + 1) + '-' + tmp.getDate();
    }
    if (format === "yyyy-MM-dd HH:mm:ss") {
        return tmp.getFullYear() + '-' + (tmp.getMonth() + 1) + '-' + tmp.getDate() + ' ' + tmp.getHours() + ':' + tmp.getMinutes() + ':' + tmp.getSeconds();
    }

}

/**
 * 获取用户角色名
 */

export function roleIdToName(roleId) {
    if (roleId === 1) {
        return "系统管理员";
    } else if (roleId === 2) {
        return "高级会员";
    } else if (roleId === 0) {
        return "普通用户";
    }
}

/**
 * RSA加密
 * @param str 待加密内容
 * @param base64PublicKey 公钥
 * @returns {string | false} 加密内容
 */

export function encrypt(str, base64PublicKey) {
    const encryptor = new JSEncrypt();
    encryptor.setPublicKey(base64PublicKey);
    return encryptor.encrypt(str);
}

export function responseIsSuccess(response) {
    return response.data.status
}

/**
 * 实现基于后端传回的Base64下载指定文件
 * @param base64 后端传回的Base64
 * @param fileName 下载文件文件名（需带文件后缀）
 * @param contextType 配置文件类型（eg：application/zip）
 */
export function getDownloadFileFromBase64(base64, fileName, contextType) {
    // 将 Base64 字符串转换为二进制数据
    const byteCharacters = atob(base64); // 解码 Base64
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    // 将字节数组转换为 Uint8Array
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], {type: contextType});
    const url = window.URL.createObjectURL(blob);
    // 创建隐藏下载链接
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', fileName)
    document.body.appendChild(link);
    link.click();
    // 清除下载链接
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url)
}