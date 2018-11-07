/*
This package contains code in part derived from scalafix.

scalafix is licensed under the BSD 3-Clause License.

Copyright (c) 2016 EPFL

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    Neither the name of the EPFL nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
import scalafix.v1._

import scala.meta.Importer

package object fix {
  // Maybe to improve, but not needed for our purposes
  def getTypeSymbol(symbol: Symbol)(implicit doc: SemanticDocument): Option[Symbol] =
    symbol.info.get.signature match {
      case MethodSignature(_, _, returnType) =>
        getSymbol(returnType)
      case ValueSignature(t) => getSymbol(t)
      case _                 => None
    }

  def getSymbol(t: SemanticType): Option[Symbol] =
    t match {
      case t: TypeRef    => Some(t.symbol)
      case t: SingleType => Some(t.symbol)
      case t: ThisType   => Some(t.symbol)
      case t: SuperType  => Some(t.symbol)
      case _             => None
    }

  def getEffectType(symbol: Symbol)(implicit doc: SemanticDocument): String =
    getType(symbol).toString
      .takeWhile(_ != '[') // There might be a better way, but for our purposes it's enough

  // From https://scalacenter.github.io/scalafix/docs/developers/semantic-type.html
  def getType(symbol: Symbol)(implicit doc: SemanticDocument): SemanticType =
    symbol.info.get.signature match {
      case MethodSignature(_, _, returnType) =>
        returnType
      case _ => NoType
    }

  def containsImport(importer: Importer)(implicit doc: SemanticDocument): Boolean =
    doc.tree
      .collect {
        case i: Importer if i == importer =>
          true
        case _ =>
          false
      }
      .exists(identity)
}
