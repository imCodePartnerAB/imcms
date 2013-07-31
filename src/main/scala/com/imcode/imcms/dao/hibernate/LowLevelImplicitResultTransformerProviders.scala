package com.imcode
package imcms.dao.hibernate

class LowLevelImplicitResultTransformerProviders {
  implicit val anyRefSingleColumnTransformerProvider = new SingleColumnTransformerProvider[AnyRef]
  implicit val stringSingleColumnTransformerProvider = new SingleColumnTransformerProvider[String]
  implicit val jIntegerSingleColumnTransformerProvider = new SingleColumnTransformerProvider[JInteger]
  implicit val jDoubleSingleColumnTransformerProvider = new SingleColumnTransformerProvider[JDouble]
  implicit val jFloatSingleColumnTransformerProvider = new SingleColumnTransformerProvider[JFloat]
  implicit val jBooleanSingleColumnTransformerProvider = new SingleColumnTransformerProvider[JBoolean]
  implicit val jCharacterSingleColumnTransformerProvider = new SingleColumnTransformerProvider[JCharacter]
  implicit val jByteSingleColumnTransformerProvider = new SingleColumnTransformerProvider[JByte]

  implicit val stringArrayResultTransformerProvider = new ArrayResultTransformerProvider[String]
  implicit val jIntegerArrayResultTransformerProvider = new ArrayResultTransformerProvider[JInteger]
  implicit val jDoubleArrayResultTransformerProvider = new ArrayResultTransformerProvider[JDouble]
  implicit val jFloatArrayResultTransformerProvider = new ArrayResultTransformerProvider[JFloat]
  implicit val jBooleanArrayResultTransformerProvider = new ArrayResultTransformerProvider[JBoolean]
  implicit val jCharacterArrayResultTransformerProvider = new ArrayResultTransformerProvider[JCharacter]
  implicit val jByteArrayResultTransformerProvider = new ArrayResultTransformerProvider[JByte]
}