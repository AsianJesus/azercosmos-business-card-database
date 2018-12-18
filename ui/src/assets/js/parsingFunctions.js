/**
 * Created by fruit on 12/18/2018.
 */
function recognizeByRegexp (text, reg) {
    let match = text.match(reg)
    let result = match ? match.filter(x => x) : [null]
    return result[result.length-1]
}
export function recognizeName (text) {
    return recognizeByRegexp(text, /[A-Z][a-z]+.*?\s[A-Z].*/)
}
export function recognizePhone (text) {
    return recognizeByRegexp(text, /(?:Pho.*?\s|Mo.*?\s)(.*)/)
}
export function recognizeEmail (text) {
    return recognizeByRegexp(text, /(?:E-?ma..\s)(.*)/)
}
export function recognizeWebsite (text) {
    return recognizeByRegexp(text, /(?:W[eB].\s)(.*)|(?:.*\s)(www.*)|(?:.*\s)(.*com)|(?:.*\s)(.*az)|(?:.*\s)(.*ru)|(?:.*\s)(.*uk)|(?:.*\s)(.*ua)|(?:.*\s)(.*net)|(?:.*\s)(.*org)/)
}