import {JSEncrypt} from "jsencrypt";

/**
 * 时间格式化
 * @param date long
 * @param format yyyy-MM-dd yyyy-MM-dd HH:mm:ss
 * @returns {string} 指定格式
 */
export function dateToString(date, format) {
    const tmp = new Date(date);
    if(format === "yyyy-MM-dd"){
        return tmp.getFullYear() + '-' + (tmp.getMonth() + 1) + '-' + tmp.getDate();
    }
    if(format === "yyyy-MM-dd HH:mm:ss"){
        return tmp.getFullYear() + '-' + (tmp.getMonth() + 1) + '-' + tmp.getDate() + ' ' + tmp.getHours() + ':' + tmp.getMinutes() + ':' + tmp.getSeconds();
    }

}

/**
 * 获取用户角色名
 */

export function roleIdToName(roleId){
    if(roleId === 1){
        return "系统管理员";
    }else if(roleId === 2){
        return "高级会员";
    }else if(roleId === 0){
        return "普通用户";
    }
}

/**
 * RSA加密
 * @param str 待加密内容
 * @param base64PublicKey 公钥
 * @returns {string | false} 加密内容
 */

export function encrypt(str, base64PublicKey){
    const encryptor = new JSEncrypt();
    encryptor.setPublicKey(base64PublicKey);
    return encryptor.encrypt(str);
}

export function responseIsSuccess(response){
    return response.data.status
}