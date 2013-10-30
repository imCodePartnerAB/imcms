package com.imcode
package imcms
package admin.doc.projection.container

import _root_.imcode.server.document.index.DocumentStoredFields

import com.vaadin.data.Item

import com.imcode.imcms.vaadin.data.ReadOnlyItem


abstract class IndexedDocItem(val index: Index, val fields: DocumentStoredFields) extends Item with ReadOnlyItem
