package org.mtransit.parser.ca_halifax_transit_bus;

import static org.mtransit.commons.Constants.EMPTY;
import static org.mtransit.commons.RegexUtils.DIGITS;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// http://www.halifax.ca/opendata/
// http://www.halifax.ca/opendata/transit.php
// http://gtfs.halifax.ca/static/google_transit.zip
public class HalifaxTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new HalifaxTransitBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_EN;
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Halifax Transit";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	private static final String RTS_CP1 = "cp1";
	private static final String RTS_ECRL = "ecrl";
	private static final String RTS_ECS = "ecs";
	private static final String RTS_FV01 = "fv01";
	private static final String RTS_HWST = "hwst";
	private static final String RTS_MACK = "mack";
	private static final String RTS_MACD = "macd";
	private static final String RTS_S14 = "s14";
	private static final String RTS_SP6 = "sp6";
	private static final String RTS_SP14 = "sp14";
	private static final String RTS_SP53 = "sp53";
	private static final String RTS_SP58 = "sp58";
	private static final String RTS_SP65 = "sp65";

	private static final long RID_CP1 = 100_001L;
	private static final long RID_ECRL = 100_002L;
	private static final long RID_ECS = 100_003L;
	private static final long RID_HWST = 100_004L;
	private static final long RID_FV01 = 100_101L;
	private static final long RID_MACK = 100_005L;
	private static final long RID_MACD = 100_006L;
	private static final long RID_S14 = 100_114L;
	private static final long RID_SP6 = 100_106L;
	private static final long RID_SP14 = 100_014L;
	private static final long RID_SP53 = 100_053L;
	private static final long RID_SP58 = 100_058L;
	private static final long RID_SP65 = 100_065L;

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	@Nullable
	@Override
	public Long convertRouteIdFromShortNameNotSupported(@NotNull String routeShortName) {
		switch (routeShortName) {
		case RTS_CP1:
			return RID_CP1;
		case RTS_ECRL:
			return RID_ECRL;
		case RTS_ECS:
			return RID_ECS;
		case RTS_FV01:
			return RID_FV01;
		case RTS_HWST:
			return RID_HWST;
		case RTS_MACK:
			return RID_MACK;
		case RTS_MACD:
			return RID_MACD;
		case RTS_S14:
			return RID_S14;
		case RTS_SP6:
			return RID_SP6;
		case RTS_SP14:
			return RID_SP14;
		case RTS_SP53:
			return RID_SP53;
		case RTS_SP58:
			return RID_SP58;
		case RTS_SP65:
			return RID_SP65;
		}
		return super.convertRouteIdFromShortNameNotSupported(routeShortName);
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	private static final Pattern STARTS_WITH_START = Pattern.compile("(\\* )", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = STARTS_WITH_START.matcher(routeLongName).replaceAll(EMPTY);
		return routeLongName;
	}

	@NotNull
	@Override
	public String cleanRouteShortName(@NotNull String routeShortName) {
		if (!CharUtils.isDigitsOnly(routeShortName)) {
			routeShortName = routeShortName.toUpperCase(Locale.ENGLISH);
		}
		return super.cleanRouteShortName(routeShortName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR_BLUE = "00558C"; // BLUE (web site CSS)

	private static final String AGENCY_COLOR = AGENCY_COLOR_BLUE;

	@Nullable
	@Override
	public String fixColor(@Nullable String color) {
		if ("7476D9".equalsIgnoreCase(color)) { // purple
			return AGENCY_COLOR;
		}
		return super.fixColor(color);
	}

	@Nullable
	@Override
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
		final String rlnLC = gRoute.getRouteLongNameOrDefault().toLowerCase(Locale.ENGLISH);
		if (rlnLC.contains("metrox")) {
			return "F7C007"; // YELLOW (from route map PDF) https://www.halifax.ca/transportation/halifax-transit/routes-schedules
		} else if (rlnLC.contains("express")) {
			return "CD1F36"; // RED (from route map PDF) https://www.halifax.ca/transportation/halifax-transit/routes-schedules
		}
		return super.provideMissingRouteColor(gRoute);
	}

	@NotNull
	@Override
	public String cleanStopOriginalId(@NotNull String gStopId) {
		gStopId = CleanUtils.cleanMergedID(gStopId);
		return gStopId;
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern STARTS_WITH_RSN = Pattern.compile("(^)(\\d+)(\\w?)(\\s)", Pattern.CASE_INSENSITIVE);

	private static final Pattern ENDS_WITH_ONLY = Pattern.compile("([\\s]*only[\\s]*$)", Pattern.CASE_INSENSITIVE);

	private static final Pattern METROLINK = Pattern.compile("((^|\\W)(metrolink)(\\W|$))", Pattern.CASE_INSENSITIVE);

	private static final Pattern EXPRESS = Pattern.compile("((^|\\W)(express)(\\W|$))", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, tripHeadsign, getIgnoredWords());
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = STARTS_WITH_RSN.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = ENDS_WITH_ONLY.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = METROLINK.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = EXPRESS.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.SAINT.matcher(tripHeadsign).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private static final String SLASH = " / ";
	private static final Pattern CLEAN_STREETS_CROSSING = Pattern.compile("((\\s)*(" //
			+ "after and opposite|afteropposite|after|" //
			+ "before and opposite|beforeopposite|before|" //
			+ "in front of|" //
			+ "opposite and after|opposite and before|" //
			+ "oppositeafter|oppositebefore|opposite" //
			+ ")(\\s)*)", Pattern.CASE_INSENSITIVE);
	private static final Pattern CLEAN_CHEVRONS = Pattern.compile("(([^<]+)(<)([^>]+)(>))", Pattern.CASE_INSENSITIVE);
	private static final String CLEAN_CHEVRONS_REPLACEMENT = "$2$4";
	private static final Pattern CLEAN_BOUNDS = Pattern.compile("(\\[[^]]*])", Pattern.CASE_INSENSITIVE);

	private static final Pattern ENDS_WITH_NUMBER = Pattern.compile("([ ]*\\([\\d]+\\)$)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, gStopName, getIgnoredWords());
		gStopName = CLEAN_BOUNDS.matcher(gStopName).replaceAll(EMPTY);
		gStopName = CLEAN_CHEVRONS.matcher(gStopName).replaceAll(CLEAN_CHEVRONS_REPLACEMENT);
		gStopName = CLEAN_STREETS_CROSSING.matcher(gStopName).replaceAll(SLASH);
		gStopName = ENDS_WITH_NUMBER.matcher(gStopName).replaceAll(EMPTY);
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	private String[] getIgnoredWords() {
		return new String[]{
				"SMU", "MSVU",
		};
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		//noinspection deprecation
		final String stopId = gStop.getStopId();
		if (CharUtils.isDigitsOnly(stopId)) {
			return Integer.parseInt(stopId);
		}
		final Matcher matcher = DIGITS.matcher(stopId);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group());
		}
		throw new MTLog.Fatal("Unexpected stop ID for %s!", gStop);
	}

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		//noinspection deprecation
		final String stopId = gStop.getStopId();
		if (CharUtils.isDigitsOnly(stopId)) {
			return stopId; // using stop ID as stop code ("GoTime" number)
		}
		final Matcher matcher = DIGITS.matcher(stopId);
		if (matcher.find()) {
			return matcher.group();
		}
		throw new MTLog.Fatal("Unexpected stop code for %s!", gStop);
	}
}
