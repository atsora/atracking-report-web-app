// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.domain;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lemoinetechnologies.pulse.reporting.birt.Configuration;
import com.lemoinetechnologies.pulse.reporting.util.Pair;

/**
 * Manager of viewing session
 * 
 * @author Eric
 * @version 1.0
 * 
 * @see ViewingSession
 */
public class ViewingSessionManager
{
	/**
	 * list of active session Viewing sessions are associated to expiration date of the session.
	 */
	private Map<String, Pair<ViewingSession, Date>> sessionsMap;
	private static Logger logger = LogManager.getLogger(ViewingSessionManager.class);
	private static ViewingSessionManager instance = new ViewingSessionManager();

	private ViewingSessionManager()
	{
		this.sessionsMap = new TreeMap<String, Pair<ViewingSession, Date>>();
		logger.debug("Creation of ViewingSessionManager");
	}

	public static ViewingSessionManager getInstance()
	{
		if (instance == null) {
			instance = new ViewingSessionManager();
		}
		return instance;
	}

	/**
	 * Create new viewing session and return it
	 * @return
	 */
	public ViewingSession createSession(String httpSession, String reportName)
	{
		synchronized (sessionsMap) {
			ViewingSession session = new ViewingSession(httpSession, reportName);
			sessionsMap.put(session.getId(), new Pair<ViewingSession, Date>(session, new Date()));
			return session;
		}
	}

	/**
	 * return viewing session if it is in session map , else null
	 * @param id
	 * @return
	 */
	public ViewingSession getSession(String id)
	{
		if (id != null) {
			synchronized (sessionsMap) {
				for (Iterator<String> iterator = sessionsMap.keySet().iterator(); iterator.hasNext();) {
					String sessionId = iterator.next();
					if (sessionId.equals(id)) {
						return sessionsMap.get(sessionId).getFirst();
					}
				}
			}
			return null;
		}
		return null;
	}

	public boolean sessionExists(String id)
	{
		ViewingSession session = getSession(id);
		return (session == null) ? false : true;
	}

	/**
	 * update last access date of viewing session identify by id. Eventually purge
	 * viewing session which are obsolete
	 * 
	 * @param id
	 *          session id
	 * @param timeout
	 *          session's timeout in milliseconds
	 */
	public void refresh(String id, long timeout)
	{	
		synchronized (sessionsMap)
		{
			// Browse all sessions
			Date date = new Date();
			Set<Entry<String, Pair<ViewingSession, Date>>> entrySet = sessionsMap.entrySet();
			for (Iterator<Entry<String, Pair<ViewingSession, Date>>> iterator = entrySet.iterator(); iterator.hasNext();)
			{
				Entry<String, Pair<ViewingSession, Date>> entry = iterator.next();
				if (entry.getValue().getFirst().getState() == ViewingSession.State.UNLOCK && entry.getValue().getSecond().before(date)) {
					logger.info("Remove viewing session: " + entry.getValue().getFirst().toString());
					entry.getValue().getFirst().cleanTemporaryFile();
					iterator.remove();
				} else if (id != null && entry.getValue().getFirst().getId().equals(id)) {
					// Increase the expiration date of the current session that has not expired yet
					sessionsMap.get(id).setSecond(new Date(new Date().getTime() + timeout));
				}
			}
		}
	}
}
