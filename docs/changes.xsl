<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>
                    Changes in imCMS
                 </title>
                <link rel="stylesheet" href="changes.css"/>
            </head>
            <body>
                <xsl:apply-templates/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="changeset">
        <h1><xsl:call-template name="version"/></h1>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template name="version">
        Changes in version <xsl:value-of select="ancestor-or-self::changeset/@version"/>
    </xsl:template>

    <xsl:template name="changes-header">
        <h2>Of
            <xsl:choose>
                <xsl:when test="@importance='high'"><span class="important">importance</span></xsl:when>
                <xsl:otherwise>interest</xsl:otherwise>
            </xsl:choose>
            to
            <xsl:value-of select="@for"/>
        </h2>
    </xsl:template>

    <xsl:template match="changes">
        <xsl:call-template name="changes-header"/>
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>

    <xsl:template match="change">
        <li class="change">
            <xsl:for-each select="issue"><a name="issue_{@id}"/></xsl:for-each>
            <xsl:apply-templates select="*[not(self::issue)]"/>
            <xsl:if test="not(*[not(self::issue)][position() = last() and self::p]) and issue">
                <p>
                    (<xsl:for-each select="issue">
                         <xsl:apply-templates select="."/>
                         <xsl:if test="not(position()=last())">, </xsl:if>
                     </xsl:for-each>)
                </p>
            </xsl:if>
        </li>
    </xsl:template>

    <xsl:template match="p">
        <xsl:copy>
            <xsl:apply-templates/>
            <xsl:if test="position() = last() and ../issue">
                (<xsl:for-each select="../issue">
                     <xsl:apply-templates select="."/>
                     <xsl:if test="not(position()=last())">, </xsl:if>
                 </xsl:for-each>)
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="ul|li|dl|dt"><xsl:copy><xsl:apply-templates/></xsl:copy></xsl:template>

    <xsl:template match="dd">
        <xsl:copy>
            - <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="config|path|tag|class|package|method|variable">
        <span class="{name()}">
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <xsl:template match="attr|value">
        "<span class="{name()}"><xsl:apply-templates/></span>"
    </xsl:template>

    <xsl:template match="url|localfile">
        <a class="{name()}" href="{.}"><xsl:value-of select="."/></a>
    </xsl:template>

    <xsl:template match="replace">
        <span class="{name()}">&lt;<xsl:apply-templates/>&gt;</span>
    </xsl:template>

    <xsl:template match="issue">
        <a class="issue" href="http://bugzilla.imcode.com/show_bug.cgi?id={@id}">Issue <xsl:value-of select="@id"/></a>
    </xsl:template>

</xsl:stylesheet>
