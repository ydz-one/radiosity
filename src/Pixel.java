import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * A pixel on a hemicube surface.
 * @author zyud
 *
 */
public class Pixel {
	private Vector3D[] coords;
	private double pixelLength;
	private HemiFaces face; // enum denoting which hemicube face it is on
	private Vector3D xAxis; // runs along the side of Hemicube
	private Vector3D yAxis; // runs along the other side of Hemicube
	private Vector3D zAxis; // equal to normal of element
	private Vector3D center;
	private ArrayList<Integer> adjacentPixels; // IDs of adjacent pixels
	private int projectedPatchId;
	private double formFactor;

	public Pixel(Vector3D center, HemiFaces face, Vector3D xAxis, Vector3D yAxis,
			Vector3D zAxis, double pixelLength) {
		this.center = center;
		this.face = face;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.zAxis = zAxis;
		this.pixelLength = pixelLength;
		adjacentPixels = new ArrayList<>();
		coords = calcPixelCoords();
	}

	private Vector3D[] calcPixelCoords() {
		Vector3D axis1 = Vector3D.ZERO;
		Vector3D axis2 = Vector3D.ZERO; 
		
		switch(face) {
		case FRONT: axis1 = xAxis;
					axis2 = yAxis;
					break;
		case LEFT: axis1 = yAxis;
				   axis2 = zAxis;
				   break;
		case RIGHT: axis1 = zAxis;
					axis2 = yAxis;
					break;
		case UP: axis1 = zAxis;
				 axis2 = xAxis;
				 break;
		case DOWN: axis1 = xAxis;
				   axis2 = zAxis;
				   break;
		}

		Vector3D[] coords = Utils.getPtsFromCenter(center, axis1, axis2, pixelLength,
				pixelLength);

		return coords;
	}

	public double getpixelLength() {
		return pixelLength;
	}

	public Vector3D[] getCoords() {
		return coords;
	}

	public HemiFaces getFace() {
		return face;
	}

	public Vector3D getCenter() {
		return center;
	}
	
	public ArrayList<Integer> getAdjacentPixels(){
		return adjacentPixels;
	}

	public int getProjectedPatchId() {
		return projectedPatchId;
	}

	public double getFormFactor() {
		return formFactor;
	}
}
