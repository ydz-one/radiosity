import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * The patch class. A patch is a small portion of a surface.
 * A patch is usually a rectangle but can be any 4-sided polygon.
 * @author zyud
 */
public class Patch {
	private Vector3D[] coords; // a Vector3D array containing the coordinates
	private Vector3D normal; // 3D unit vector pointing to normal of patch
	private double reflectance; // reflectance of Patch, between 0 and 1
	private Vector3D center; // center of Patch
	private double incident; // amount of light hitting Patch
	private double excident; // amount of light leaving Patch

	public Patch(Vector3D[] coords, Vector3D normal, double reflectance) {
		this.coords = coords;
		this.normal = normal;
		this.reflectance = reflectance;
		center = calcCenter(coords);
		incident = 0.0;
		excident = 0.0;
	}
	
	/**
	 * VERIFY WITH AUTOCAD!!!
	 * Calculates the coords of the center point of a Patch.
	 * @param coords
	 * @return coords of the center point
	 */
	private Vector3D calcCenter(Vector3D[] coords) {
		Vector3D pt1 = coords[0];
		Vector3D pt2 = coords[1];
		Vector3D pt3 = coords[2];
		Vector3D pt4 = coords[3];
		
		Vector3D center = calcMid(calcMid(pt1, pt2), calcMid(pt3, pt4));

		return center;
	}
	
	/**
	 * VERIFY WITH AUTOCAD!!!
	 * Calculates the coords of the midpoint between two points
	 * @param pt1
	 * @param pt2
	 * @return coords of the midpoint
	 */
	private Vector3D calcMid(Vector3D pt1, Vector3D pt2) {
		double midpointX = (pt1.getX() + pt2.getX()) / 2;
		double midpointY = (pt1.getY() + pt2.getY()) / 2;
		double midpointZ = (pt1.getZ() + pt2.getZ()) / 2;

		return new Vector3D(midpointX, midpointY, midpointZ);
	}

	public Vector3D[] getCoords() {
		return coords;
	}

	public Vector3D getNormal() {
		return normal;
	}

	public double getReflectance() {
		return reflectance;
	}

	public Vector3D getCenter() {
		return center;
	}

	public double getIncident() {
		return incident;
	}

	public double getExcident() {
		return excident;
	}
}
