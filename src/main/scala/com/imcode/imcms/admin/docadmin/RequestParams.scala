package com.imcode
package imcms
package admin.docadmin

import java.nio.charset.StandardCharsets
import com.vaadin.server.{Page, VaadinRequest}
import org.apache.http.client.utils.URLEncodedUtils
import scala.collection.JavaConverters._

// Embedded Vaadin Workaround:
// When Vaadin is running in embedded mode,  VaadinRequest#getParameter(String) (as passed to UI#init)
// and related methods fail to return actual HTTP GET request parameters (as requested by browser),
// instead, they are wrapped inside "v-loc" parameter.
// Query string can be obtained using Page.getCurrent().getLocation().getQuery() method.

class RequestParams(request: VaadinRequest) {

  private[this] val embeddedParamsMap: Map[String, Seq[String]] = URLEncodedUtils.parse(Page.getCurrent.getLocation.getQuery, StandardCharsets.UTF_8)
    .asScala
    .groupBy(pair => pair.getName)
    .mapValues(pairs => pairs.map(_.getValue).toSeq)

  private[this] val embeddedParams: Map[String, String] = embeddedParamsMap.mapValues(seq => seq.head)

  def apply(name: String): String = value(name)

  def value(name: String): String = request.getParameter(name).trimToOption.orElse(embeddedParams.get(name)).orNull

  def values(name: String): Seq[String] = request.getParameterMap.get(name) match {
    case array if array.isEmpty => embeddedParamsMap(name)
    case array => array.toSeq
  }
}
