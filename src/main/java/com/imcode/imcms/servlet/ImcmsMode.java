package com.imcode.imcms.servlet;

/**
 * Imcms running mode.
 */
public enum ImcmsMode {

    /** All core services and functionality is available. */
    NORMAL,

    /** All/Some core services or/and functionality might not be available. */
    MAINTENANCE
}