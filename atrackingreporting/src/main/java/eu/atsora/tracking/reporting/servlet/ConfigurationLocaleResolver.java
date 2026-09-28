// Copyright (C) 2026 Atsora Solutions
//
// SPDX-License-Identifier: EPL-2.0

package eu.atsora.tracking.reporting.servlet;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import eu.atsora.tracking.reporting.birt.Configuration;
import eu.atsora.tracking.reporting.util.Utils;

/**
 * Session locale resolver whose default locale is BIRT_LOCALE from the configuration
 *
 * The locale parameter of the URL (LocaleChangeInterceptor) still takes precedence.
 * The configuration is read at each request because it may be changed from the configuration page.
 */
public class ConfigurationLocaleResolver extends SessionLocaleResolver {

  @Override
  protected Locale determineDefaultLocale(HttpServletRequest request)
  {
    return Utils.getLocale(Configuration.BIRT_LOCALE);
  }
}
