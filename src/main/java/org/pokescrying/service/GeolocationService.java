package org.pokescrying.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;

@ApplicationScope
@Component
public class GeolocationService {
	private static final double PI = 3.14159265;
	
    private static final double TWOPI = 2*PI;

	private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);
	
    private Map<String, List<Coordinate>> fences = null;
    
    @Value("${events.fences}")
	private String fenceResources;
    
    @PostConstruct
	public void initFences() {
		fences = new HashMap<>();
		LOGGER.info("Initializing geolocation services using fences from: {} ", fenceResources);
		
		for (String resource : fenceResources.split(",")) {
			LOGGER.debug("Reading fences from: {} ", resource);
			Kml kml = Kml.unmarshal(GeolocationService.class.getResourceAsStream(resource));
			Document document = (Document)kml.getFeature();
			
			for (var feature : document.getFeature()) {
				if (feature instanceof Placemark) {
					Placemark placemark = (Placemark)feature;
					Polygon geometry = (Polygon)placemark.getGeometry();
					LOGGER.debug("Putting fence '{}' with coordinates {}", placemark.getName(), geometry.getOuterBoundaryIs().getLinearRing().getCoordinates());
					fences.put(placemark.getName(), geometry.getOuterBoundaryIs().getLinearRing().getCoordinates());
				}
			}
		}
	}
	
	public boolean isCoordinateInsideFence(double latitude, double longitude, String fenceName) {
		return isCoordinateInsidePolygon(latitude, longitude, fences.get(fenceName));
	}
	
	private boolean isCoordinateInsidePolygon(double latitude, double longitude, List<Coordinate> polygon) {
		int i;
		double angle = 0;
		double point1Lat;
		double point1Long;
		double point2Lat;
		double point2Long;
		int n = polygon.size();

		for (i = 0; i < n; i++) {
			point1Lat = polygon.get(i).getLatitude() - latitude;
			point1Long = polygon.get(i).getLongitude() - longitude;
			point2Lat = polygon.get((i + 1) % n).getLatitude() - latitude;
			point2Long = polygon.get((i + 1) % n).getLongitude() - longitude;
			angle += calculateAngle2D(point1Lat, point1Long, point2Lat, point2Long);
		}

		return Math.abs(angle) >= PI;
	}

	private static double calculateAngle2D(double y1, double x1, double y2, double x2) {
		double theta1 = Math.atan2(y1, x1);
		double theta2 = Math.atan2(y2, x2);
		double dtheta = theta2 - theta1;

		while (dtheta > PI)
			dtheta -= TWOPI;
		while (dtheta < -PI)
			dtheta += TWOPI;

		return (dtheta);
	}
}