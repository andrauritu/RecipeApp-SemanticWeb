<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="userSkill"/>

    <xsl:output method="html" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/">
        <div>
            <table class="table table-bordered align-middle">
                <thead class="table-dark">
                    <tr>
                        <th>Title</th>
                        <th>Cuisine Types</th>
                        <th>Difficulty</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <xsl:for-each select="recipes/recipe">
                        <xsl:variable name="bgColor">
                            <xsl:choose>
                                <xsl:when test="difficulty = $userSkill">background-color: #fff3cd !important</xsl:when>
                                <xsl:otherwise>background-color: #d1e7dd !important</xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <tr>
                            <td style="{$bgColor}"><xsl:value-of select="title"/></td>
                            <td style="{$bgColor}">
                                <xsl:value-of select="cuisineTypes/cuisineType[1]"/>
                                <xsl:if test="cuisineTypes/cuisineType[2]">
                                    <xsl:text>, </xsl:text>
                                    <xsl:value-of select="cuisineTypes/cuisineType[2]"/>
                                </xsl:if>
                            </td>
                            <td style="{$bgColor}"><xsl:value-of select="difficulty"/></td>
                            <td style="{$bgColor}">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>/recipes/</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="class">btn btn-sm btn-outline-primary</xsl:attribute>
                                    View Details
                                </a>
                            </td>
                        </tr>
                    </xsl:for-each>
                </tbody>
            </table>
        </div>
    </xsl:template>

</xsl:stylesheet>
