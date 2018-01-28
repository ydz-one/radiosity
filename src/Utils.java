import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Utility class.
 * @author zyud
 *
 */
public final class Utils {
	
	private Utils() {} // private constructor method to avoid instantiation
	
	/**
	 * Creates the four corner pt of rectangle in 3D space, given the center pt.
	 * @param center coord of the center pt
	 * @param axis1 unit vector of first axis
	 * @param axis2 unit vector of second axis
	 * @param sideLen1 length of side along axis1
	 * @param sideLen2 length of side along axis2
	 * @return array of 4 Vector3D objects, each representing a pt
	 */
	public static final Vector3D[] getPtsFromCenter(Vector3D center, Vector3D axis1,
			Vector3D axis2, double sideLen1, double sideLen2) {

		Vector3D[] coords = new Vector3D[4];

		for (int i = 0; i < coords.length; i++) {
			coords[i] = center;
		}

		int k = 0;
		for (int i = -1; i <= 1; i += 2) {
			for (int j = -1; j <= 1; j += 2) {
				coords[k] = coords[k].add(axis1.scalarMultiply(i * sideLen1 / 2));
				coords[k] = coords[k].add(axis2.scalarMultiply(j * sideLen2 / 2));
				k++;
			}
		}
		return coords;
	}

	/**
	 * Evaluates whether a point on the same plane as a rectangle in 3D space is
	 * within the bounds of that rectangle.
	 * @param pt point to be evaluated
	 * @param center center point of rectangle
	 * @param axis1 axis along with one side of the rectangle extends
	 * @param axis2 axis along with the other side of the rectangle extends
	 * @param sideLen1 length of side along axis1
	 * @param sideLen2 length of side along axis2
	 * @return 0 if within bounds, not 0 otherwise. See comments for out of bound
	 * conditions
	 */
	public static final int withinBounds(Vector3D pt, Vector3D center, 
			Vector3D axis1, Vector3D axis2, double sideLen1, double sideLen2, 
			double tolerance) {

		// check that pt and center are on the same plane
		Vector3D axis3 = axis1.crossProduct(axis2);

		double ptAxis3 = pt.dotProduct(axis3);
		double centerAxis3 = center.dotProduct(axis3);
		
		if (!doublesAreEqual(ptAxis3, centerAxis3, tolerance)) {
			System.out.println("withinBounds() error: point and center not on "
					+ "the same plane.");
			return 5;
		}

		// get vector from center to pt
		Vector3D ptVector = pt.subtract(center);

		// get component of ptVector projected onto axes
		double distAxis1 = ptVector.dotProduct(axis1);
		double distAxis2 = ptVector.dotProduct(axis2);

		// compare magnitude values with square sideLength
		if (!doublesAreEqual(distAxis1, sideLen1 / 2, tolerance) && 
				distAxis1 > (sideLen1 / 2) && !doublesAreEqual(distAxis2, 
						sideLen2 / 2, tolerance) && distAxis2 > (sideLen2 / 2)) {
			return 4; // pt exceeds axis1 and 2 bounds in the +ve direction
		} else if (!doublesAreEqual(distAxis1, -1 * sideLen1 / 2, tolerance) && 
					distAxis1 < (-1 * sideLen1 / 2) && !doublesAreEqual(distAxis2, 
							-1 * sideLen2 / 2, tolerance) && distAxis2 < (-1 * 
									sideLen2 / 2)) {
			return -4; // pt exceeds axis1 and 2 bounds in the -ve direction
		} else if (!doublesAreEqual(distAxis1, sideLen1 / 2, tolerance) && 
				distAxis1 > (sideLen1 / 2) && !doublesAreEqual(distAxis2, 
						-1 * sideLen2 / 2, tolerance) && distAxis2 < (-1 * 
								sideLen2 / 2)) {
			return 3; // pt exceeds axis1 in the +ve direction and axis2 in the
			// -ve direction
		} else if (!doublesAreEqual(distAxis1, -1 * sideLen1 / 2, tolerance) && 
					distAxis1 < (-1 * sideLen1 / 2) && !doublesAreEqual(distAxis2, 
							sideLen2 / 2, tolerance) && distAxis2 > (sideLen2 / 2)) {
			return -3; // pt exceeds axis1 in the -ve direction and axis2 in the
			// +ve direction
		} else if (!doublesAreEqual(distAxis1, sideLen1 / 2, tolerance) && 
				distAxis1 > (sideLen1 / 2)) {
			return 1; // pt exceeds axis1 bounds in the +ve direction
		} else if (!doublesAreEqual(distAxis1, -1 * sideLen1 / 2, tolerance) && 
				distAxis1 < (-1 * sideLen1 / 2)) {
			return -1; // pt exceeds axis1 bounds in the -ve direction
		} else if (!doublesAreEqual(distAxis2, sideLen2 / 2, tolerance) && 
				distAxis2 > (sideLen2 / 2)) {
			return 2; // pt exceeds axis2 bounds in the +ve direction
		} else if (!doublesAreEqual(distAxis2, -1 * sideLen2 / 2, tolerance) && 
				distAxis2 < (-1 * sideLen2 / 2)) {
			return -2; // pt exceeds axis2 bounds in the -ve direction
		} else {
			return 0; // return 0 if within bounds
		}
	}

	/**
	 * Evaluates if two doubles are equal, given a specific tolerance.
	 * @param a
	 * @param b
	 * @param tolerance
	 * @return true if equal, false otherwise
	 */
	public static final boolean doublesAreEqual(double a, double b, double tolerance) {
		double c = a - b;
		if (c <= tolerance && c >= -1 * tolerance) {
			return true;
		} else {
			return false;
		}		
	}
	
	/**
	 * Evaluates if two pts have the same value along a specific axis.
	 * @param pt1
	 * @param pt2
	 * @param axis
	 * @return true if two pts have the same value along axis, false otherwise
	 */
	public static final boolean equalAlongAxis(Vector3D pt1, Vector3D pt2,
			Vector3D axis, double tolerance) {
		// finding componet of pt vectors on axis
		double pt1ProjAxis = pt1.dotProduct(axis);
		double pt2ProjAxis = pt2.dotProduct(axis);

		return doublesAreEqual(pt1ProjAxis, pt2ProjAxis, tolerance);		
	}
}
