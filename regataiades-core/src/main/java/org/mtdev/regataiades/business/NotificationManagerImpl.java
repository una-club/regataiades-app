package org.mtdev.regataiades.business;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.mtdev.regataiades.business.interfaces.DataRenderer;
import org.mtdev.regataiades.business.interfaces.MailManager;
import org.mtdev.regataiades.business.interfaces.NotificationManager;
import org.mtdev.regataiades.model.Crew;
import org.mtdev.regataiades.model.MealBooking;
import org.mtdev.regataiades.model.Team;
import org.mtdev.regataiades.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationManagerImpl implements NotificationManager {

	private static final int sPricePerAthlete = 10;

	private static final float sPricePerMeal = 10.0f;

	protected String mLang = "fr";

	@Autowired
	protected MailManager mMailManager;

	@Autowired
	protected DataRenderer mDataRenderer;

	@Override
	public boolean setLanguage(String pLang) {
		mLang = pLang;
		return false;
	}

	@Override
	public boolean notifyAccountCreation(User pUser, Team pTeam) {
		String lUsername = pUser.getLogin();
		String lPassword = pUser.getPassword();
		Map<Object, Object> lContext = new HashMap<Object, Object>();
		lContext.put("password", lPassword);
		lContext.put("username", lUsername);
		if (pTeam != null)
			lContext.put("invited", pTeam.isInvited());

		Writer lOutput = mDataRenderer.renderData(lContext,
				"/templates/mail/mailAccountCreation." + mLang + ".html");

		String lSubject = getSubject("account");
		mMailManager.sendMail(pUser.getLogin(), "[Regataiades] " + lSubject,
				lOutput.toString());

		return true;
	}

	@Override
	public boolean notifyRegistrationUpdate(Team pTeam) {
		return notifyRegistrationOperation(pTeam, true);
	}

	/**
	 * FIXME Put price calc elsewhere
	 */
	@Override
	public boolean notifyFirstRegistration(Team pTeam) {
		return notifyRegistrationOperation(pTeam, false);
	}

	public boolean notifyRegistrationOperation(Team pTeam, boolean pUpdate) {
		Map<Object, Object> lContext = new HashMap<Object, Object>();
		lContext.put("payementLabel", "Regataiades Equipe " + pTeam.getName());
		lContext.put("invited", pTeam.isInvited());
		lContext.put("name", pTeam.getName());
		lContext.put("contactName", pTeam.getContactName());
		lContext.put("contactSurname", pTeam.getContactSurname());
		lContext.put("contactEmail", pTeam.getContactEmail());

		lContext.put("cost", pTeam.getAthletesNum() * sPricePerAthlete);

		Map<String, Integer> lCrewsCnt = new HashMap<String, Integer>();
		for (Crew lCrew : pTeam.getCrews()) {
			String lCat = lCrew.getCategory();
			Integer lCnt = 1;
			if (lCrewsCnt.containsKey(lCat)) {
				lCnt = lCrewsCnt.get(lCat) + 1;
			}
			lCrewsCnt.put(lCat, lCnt);
		}

		lContext.put("crewsCnt", lCrewsCnt);

		String lTemplate = (pUpdate) ? "mailUpdateNotification"
				: "mailCreationNotification";

		Writer lOutput = mDataRenderer.renderData(lContext, "/templates/mail/"
				+ lTemplate + "." + mLang + ".html");
		Writer lSystemOutput = mDataRenderer.renderData(lContext,
				"/templates/mail/mailSystemNotification.html");

		String lSubject = getSubject((pUpdate) ? "updateRegistration"
				: "firstRegistration");
		mMailManager.sendMail(pTeam.getContactEmail(), "[Regataiades] "
				+ lSubject, lOutput.toString());

		String lSystemContent = lSystemOutput.toString();
		mMailManager.sendMail("mishgunn@gmail.com",
				"[Regataiades] Nouvelle inscription", lSystemContent);

		mMailManager.sendMail("inscriptions@regataiades.fr",
				"[Regataiades] Nouvelle inscription", lSystemContent);

		return true;
	}

	@Override
	public boolean notifyMealBooking(Team pTeam, MealBooking pMealBooking) {
		Map<Object, Object> lContext = new HashMap<Object, Object>();
		lContext.put("payementLabel", "Regataiades Repas Equipe " + pTeam.getName());
		lContext.put("invited", pTeam.isInvited());
		lContext.put("name", pTeam.getName());
		lContext.put("contactName", pTeam.getContactName());
		lContext.put("contactSurname", pTeam.getContactSurname());
		lContext.put("mealBooking", pMealBooking);

		float lPrice = (pMealBooking.getSaturdayNoon()
				+ pMealBooking.getSaturdayNight() + pMealBooking
					.getSundayNoon()) * sPricePerMeal;

		lContext.put("cost", lPrice);

		String lTemplate = "mailMealBookingNotification";

		Writer lOutput = mDataRenderer.renderData(lContext, "/templates/mail/"
				+ lTemplate + "." + mLang + ".html");

		String lSubject = getSubject("mealBooking");
		String lMessageContent = lOutput.toString();
		mMailManager.sendMail(pTeam.getContactEmail(), "[Regataiades] "
				+ lSubject, lMessageContent);

		mMailManager.sendMail("mishgunn@gmail.com",
				"[Regataiades] Nouvelle reservation du repas", lMessageContent);

		mMailManager.sendMail("inscriptions@regataiades.fr",
				"[Regataiades] Nouvelle inscription", lMessageContent);

		return true;
	}

	/*
	 * FIXME Extremely dirty. Do refactor
	 */
	protected String getSubject(String pLabel) {
		if (pLabel.compareTo("account") == 0) {
			if (isFrench()) {
				return "Votre compte utilisateur";
			} else {
				return "Your user account";
			}
		} else if (pLabel.compareTo("firstRegistration") == 0
				|| pLabel.compareTo("updateRegistration") == 0) {
			if (isFrench()) {
				return "Votre inscription à la régate";
			} else {
				return "Your registration to the regatta";
			}
		} else if (pLabel.compareTo("mealBooking") == 0) {
			if (isFrench()) {
				return "Réservation des repas";
			} else {
				return "Meals booking";
			}
		}
		return null;
	}

	protected boolean isFrench() {
		return mLang.compareTo("fr") == 0;
	}

}
