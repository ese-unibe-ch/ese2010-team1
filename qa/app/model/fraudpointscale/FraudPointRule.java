package model.fraudpointscale;

import models.MajorEntry;

interface FraudPointRule {
	void check(MajorEntry entry);
}
