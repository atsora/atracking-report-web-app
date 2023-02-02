<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<input id="fmt_scheduler" type="hidden" value="<spring:message code='scheduler.report' />" />
<div id="scheduledialogbox">
	<div id="scheduletab">
		<ul>
			<li><a href="#scheduleonce"><spring:message code="scheduleonce" /> </a></li>
			<li><a href="#scheduledaily"><spring:message code="scheduledaily" /> </a></li>
			<li><a href="#scheduleweekly"><spring:message code="scheduleweekly" /> </a></li>
			<li><a href="#schedulemonthly"><spring:message code="schedulemonthly" /> </a></li>
		</ul>
		<div id="scheduleonce">
			<spring:message code="starttime" />
			&nbsp;&nbsp;<input type="text" name="datepickeronce" id="datepickeronce" /> &nbsp;&nbsp;<input type="text" name="timepickeronce" id="timepickeronce" />
		</div>
		<div id="scheduledaily">
			<table>
				<tr>
					<td><spring:message code="starttime" /></td>
					<td><input type="text" name="timepickerdaily" id="timepickerdaily" /></td>
				</tr>
				<tr>
					<td><spring:message code="every" /></td>
					<td><input name="everydayspinner" id="everydayspinner" name="value" value="1" />&nbsp;&nbsp;<spring:message code="day_s" /></td>
				</tr>
			</table>
		</div>
		<div id="scheduleweekly">
			<table>
				<tr>
					<td><spring:message code="starttime" /></td>
					<td><input type="text" name="timepickerweekly" id="timepickerweekly" /></td>
				</tr>
				<tr>
					<td><spring:message code="every" /></td>
					<td><input name="everyweekspinner" id="everyweekspinner" name="value" value="1" />&nbsp;&nbsp;<spring:message code="week_s" /></td>
				</tr>
				<tr>
					<td><spring:message code="on" /></td>
					<td><select id="scheduleweeklyday">
							<option value="monday">
								<spring:message code="monday" />
							</option>
							<option value="tuesday">
								<spring:message code="tuesday" />
							</option>
							<option value="wednesday">
								<spring:message code="wednesday" />
							</option>
							<option value="thursday">
								<spring:message code="thursday" />
							</option>
							<option value="friday">
								<spring:message code="friday" />
							</option>
							<option value="saturday">
								<spring:message code="saturday" />
							</option>
							<option value="sunday">
								<spring:message code="sunday" />
							</option>
					</select>
					</td>
				</tr>
			</table>
		</div>
		<div id="schedulemonthly">
			<table>
				<tr>
					<td><spring:message code="starttime" /></td>
					<td><input type="text" name="timepickermonthly" id="timepickermonthly" /></td>
				</tr>
				<tr>
					<td colspan="2">
						<table>
							<tr>
								<td><input type="radio" name="monthperiod" id="dayofmonth" value="dayofmonth" checked />&nbsp;&nbsp;<spring:message code="day" />&nbsp;&nbsp; <input
									id="dayofmonthspinner" name="value" value="1" />&nbsp;&nbsp;<spring:message code="ofthemonth_s" />
								</td>
							</tr>
							<tr>
								<td><input type="radio" name="monthperiod" id="pointofmonth" value="pointofmonth" />&nbsp;&nbsp;<spring:message code="the" />&nbsp;&nbsp; <select
									id="pointofmonthday">
										<option value="first">
											<spring:message code="first" />
										</option>
										<option value="second">
											<spring:message code="second" />
										</option>
										<option value="third">
											<spring:message code="third" />
										</option>
										<option value="fourth">
											<spring:message code="fourth" />
										</option>
										<option value="last">
											<spring:message code="last" />
										</option>
								</select>&nbsp;&nbsp; <select id="schedulemonthlyday">
										<option value="monday">
											<spring:message code="monday" />
										</option>
										<option value="tuesday">
											<spring:message code="tuesday" />
										</option>
										<option value="wednesday">
											<spring:message code="wednesday" />
										</option>
										<option value="thursday">
											<spring:message code="thursday" />
										</option>
										<option value="friday">
											<spring:message code="friday" />
										</option>
										<option value="saturday">
											<spring:message code="saturday" />
										</option>
										<option value="sunday">
											<spring:message code="sunday" />
										</option>
								</select>&nbsp;&nbsp; <spring:message code="ofthemonth_s" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<fieldset>
							<legend>
								<spring:message code="selestionofthemonth" />
							</legend>
							<table style="width: 100%">
								<tr>
									<td style="width: 33%"><span><input type="checkbox" name="january" checked />&nbsp;<spring:message code="january" /> </span></td>
									<td style="width: 33%"><span><input type="checkbox" name="february" checked />&nbsp;<spring:message code="february" /> </span></td>
									<td style="width: 33%"><span><input type="checkbox" name="march" checked />&nbsp;<spring:message code="march" /> </span></td>
								</tr>
								<tr>
									<td style="width: 33%"><span><input type="checkbox" name="april" checked />&nbsp;<spring:message code="april" /> </span></td>
									<td style="width: 33%"><span><input type="checkbox" name="may" checked />&nbsp;<spring:message code="may" /> </span></td>
									<td style="width: 33%"><span><input type="checkbox" name="june" checked />&nbsp;<spring:message code="june" /> </span></td>
								</tr>
								<tr>
									<td style="width: 33%"><span><input type="checkbox" name="july" checked />&nbsp;<spring:message code="july" /> </span></td>
									<td style="width: 33%"><span><input type="checkbox" name="august" checked />&nbsp;<spring:message code="august" /> </span></td>
									<td style="width: 33%"><span><input type="checkbox" name="september" checked />&nbsp;<spring:message code="september" /> </span></td>
								</tr>
								<tr>
									<td style="width: 33%"><span><input type="checkbox" name="october" checked />&nbsp;<spring:message code="october" /> </span></td>
									<td style="width: 33%"><span><input type="checkbox" name="november" checked />&nbsp;<spring:message code="november" /> </span></td>
									<td style="width: 33%"><span><input type="checkbox" name="december" checked />&nbsp;<spring:message code="december" /> </span></td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>

<script type="text/javascript">
	
</script>