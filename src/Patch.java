/**
 * The patch class. A patch is a small portion of a surface.
 * A patch is usually a rectangle but can be any 4-sided polygon.
 * @author zyud
 */
public class Patch {
	private double[][] coords; // 4x3 array of the x,y,z coords of the 4 corner pts
	// the coords array stores adjacent corner points of the Patch in a 
	// counter-clockwise manner. (e.g. top left, bottom left, bottom right, top right)
	private double[] normal; // 3D unit vector pointing to normal of patch
	private double reflectance; // reflectance of Patch, between 0 and 1
	private double[] center; // center of Patch
	private double incident; // amount of light hitting Patch
	private double excident; // amount of light leaving Patch

	public Patch(double[][] coords, double[] normal, double reflectance) {
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
	private double[] calcCenter(double[][] coords) {
		double[] center = new double[3];
		double[] pt1 = coords[0];
		double[] pt2 = coords[1];
		double[] pt3 = coords[2];
		double[] pt4 = coords[3];
		
		center = calcMid(calcMid(pt1, pt2), calcMid(pt3, pt4));

		return center;
	}
	
	/**
	 * VERIFY WITH AUTOCAD!!!
	 * Calculates the coords of the midpoint between two points
	 * @param pt1
	 * @param pt2
	 * @return coords of the midpoint
	 */
	private double[] calcMid(double[] pt1, double[] pt2) {
		double[] midpoint = new double[3];

		midpoint[0] = (pt1[0] + pt2[0]) / 2;
		midpoint[1] = (pt1[1] + pt2[1]) / 2;
		midpoint[2] = (pt1[2] + pt2[2]) / 2;

		return midpoint;
	}

	public double[][] getCoords() {
		return coords;
	}

	public double[] getNormal() {
		return normal;
	}

	public double getReflectance() {
		return reflectance;
	}

	public double[] getCenter() {
		return center;
	}

	public double getIncident() {
		return incident;
	}

	public double getExcident() {
		return excident;
	}
}
