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
        'rus': /(?:\u0422[\u0415\u0435][\u041b\u043b].*?\s|\u041c[\u041E\u043E][\u0411\u0431].*?\s)(.*)/
    },
    'email': {
        'eng': /^\S+@\S+$/
    },
    'website': {
        'eng': /(?:W[eB].\s)(.*)|(?:.*\s)(www.*)|(?:.*\s)(.*com)|(?:.*\s)(.*az)|(?:.*\s)(.*ru)|(?:.*\s)(.*uk)|(?:.*\s)(.*ua)|(?:.*\s)(.*net)|(?:.*\s)(.*org)/
    }
}

export function recognize (text, type, lang = null) {
    if(!lang || !lang in regexs[type])
    {
        lang = 'eng'
    }
    return recognizeByRegexp(text, regexs[type][lang])
}