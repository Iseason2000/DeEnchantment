/*
 * Description:
 * @Author: Iseason2000
 * @Date: 2022/1/28 下午8:35
 *
 */

package top.iseason.kotlin.deenchantment.utils.kparser

class BadSyntaxException(msg: String = "Bad Syntax") : Exception(msg)

class DomainException(msg: String = "Domain Error") : Exception(msg)

class ImaginaryException(msg: String = "Imaginary Number not supported") : Exception(msg)

class BaseNotFoundException(msg: String = "Base Not Found") : Exception(msg)