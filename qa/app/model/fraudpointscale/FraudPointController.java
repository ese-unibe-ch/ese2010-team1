package model.fraudpointscale;

import java.util.Date;
import java.util.List;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

public class FraudPointController extends Model {
	private final static long expiration = 24 * 60 * 60 * 1000;

	@OneToOne
	private Date lastRun;

	@OneToMany
	private List<FraudPoint> fraudPoints;

	public static FraudPointController getInstance() {
		FraudPointController instance = FraudPointController.all().first();
		if (instance != null) {
			return instance;
		} else {
			return new FraudPointController();
		}
	}

	private FraudPointController() {

	}

	public void run() {
		cleanOldPoints();
		lastRun = new Date();
	}

	public void cleanOldPoints() {
		Date expired = new Date(new Date().getTime() - expiration);
		for (FraudPoint point : fraudPoints) {
			if (point.timestamp.before(expired)) {
				point.delete();
			}
		}
	}
}
