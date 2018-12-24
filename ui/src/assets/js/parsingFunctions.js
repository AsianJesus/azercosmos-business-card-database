/**
 * Created by fruit on 12/18/2018.
 */

function recognizeByRegexp (text, reg) {
    let match = text.match(reg)
    let result = match ? match.filter(x => x) : [null]
    return result[result.length-1]
}

const regexs = {
    'name': {
        'eng': /[A-Z][a-z]+.*?\s[A-Z].*/,
        'rus': /[\u0410-\u042F][\u0430-\u044F]+.*?\s[\u0410-\u042F]/
    },
    'phone': {
        'eng': /(?:Pho.*?\s|Mo.*?\s)(.*)/,
        'rus': /(?:\u0422[\u0415\u0435][\041B\043B].*?\s|\u041c[\041E\u043E][\u0411\u0431].*?\s)(.*)/
    },
    'email': {
        'eng': /(?:E-?ma..\s)(.*)|.*@.*/,
        'rus': /.*@.*/
    },
    'website': {
        'eng': /(?:W[eB].\s)(.*)|(?:.*\s)(www.*)|(?:.*\s)(.*com)|(?:.*\s)(.*az)|(?:.*\s)(.*ru)|(?:.*\s)(.*uk)|(?:.*\s)(.*ua)|(?:.*\s)(.*net)|(?:.*\s)(.*org)/
    }
}

export function recognizeName (text, lang = null) {
    if(!lang || !lang in regexs['name'])
    {
        lang = 'eng'
    }
    return recognizeByRegexp(text, regexs['name'][lang])
}
export function recognizePhone (text, lang = null) {
    if(!lang || !lang in regexs['phone'])
    {
        lang = 'eng'
    }
    return recognizeByRegexp(text, regexs['phone'][lang])
}
export function recognizeEmail (text, lang = null) {
    if(!lang || !lang in regexs['email'])
    {
        lang = 'eng'
    }
    return recognizeByRegexp(text, regexs['email'][lang])
}
export function recognizeWebsite (text, lang = null) {
    if(!lang || !lang in regexs['website'])
    {
        lang = 'eng'
    }
    return recognizeByRegexp(text, regexs['website'][lang])
}