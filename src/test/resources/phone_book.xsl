<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:template match="/">
        <xsl:for-each select="PHONEBOOK/PERSON">

            <td><xsl:value-of select="NAME"/></td>
            <td><xsl:value-of select="TELEPHONE"/></td>
            <td><xsl:value-of select="EMAIL"/></td>

        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>