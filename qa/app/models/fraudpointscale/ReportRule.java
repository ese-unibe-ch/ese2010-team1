package models.fraudpointscale;

import java.util.Date;
import java.util.List;

import models.Report;

public class ReportRule extends FraudPointRule {

	/**
	 * Checks if there are any new reports since the lastCheck and if so it
	 * creates new FraudePoints for them
	 * 
	 * @param the
	 *            lastCheck Date
	 */
	@Override
	public void checkSince(Date lastCheck) {
		List<Report> reports = Report.findAll();
		reports = deleteOld(reports, lastCheck);
		for (Report report : reports) {
			new FraudPoint(report.entry.owner, this.getClass()).save();
		}
	}

	/**
	 * Deletes the MajorEntry which are too old out of a List.
	 * 
	 * @param entrys
	 *            the complete List
	 * @param date
	 *            the Date
	 * @return the up-to-date list
	 */
	public List<Report> deleteOld(List<Report> reports, Date date) {
		for (Report report : reports) {
			if (report.timeStamp.before(date))
				reports.remove(report);
		}
		return reports;

	}

}
