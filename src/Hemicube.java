import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * A hemicube class used to calculate form factor for each element.
 * @author zyud
 *
 */
public class Hemicube {
	private static final double SIDELEN = 0.5; // side length is 6" for all hemicubes
	private static final double TOLERANCE = 0.00000025; //0.01% error tolerance
	private static final double PIXELLEN = 0.0025; // 200x200 pixels per 6"x6" face
	private Vector3D origin;
	private Vector3D xAxis; // extends to left when facing direction of normal
	private Vector3D yAxis; // extends up when facing direction of normal
	private Vector3D zAxis; // extends perpendicular out from surface, same as normal
	private Vector3D frontCtrPt;
	private Vector3D leftCtrPt;
	private Vector3D rightCtrPt;
	private Vector3D upCtrPt;
	private Vector3D downCtrPt;
	private Vector3D[] frontFacePts;
	private Vector3D[] leftFacePts;
	private Vector3D[] rightFacePts;
	private Vector3D[] upFacePts;
	private Vector3D[] downFacePts;
	private Plane frontFace;
	private Plane leftFace;
	private Plane rightFace;
	private Plane upFace;
	private Plane downFace;
	private int pixelIdCount;
	private HashMap<Integer, Pixel> allPixelIdMap;
	private HashMap<Vector3D, Integer> pixelCoordMap;
	private HashMap<Integer, ArrayList<Integer>> frontPixelsMap;
	private HashMap<Integer, ArrayList<Integer>> leftPixelsMap;
	private HashMap<Integer, ArrayList<Integer>> rightPixelsMap;
	private HashMap<Integer, ArrayList<Integer>> upPixelsMap;
	private HashMap<Integer, ArrayList<Integer>> downPixelsMap;
	
	public Hemicube(Vector3D origin, Vector3D zAxis) {
		this.origin = origin;
		this.zAxis = zAxis.normalize();
		xAxis = zAxis.orthogonal();
		yAxis = Vector3D.crossProduct(zAxis, xAxis);
		frontFacePts = new Vector3D[4];
		leftFacePts = new Vector3D[4];
		rightFacePts = new Vector3D[4];
		upFacePts = new Vector3D[4];
		downFacePts = new Vector3D[4];
		createFaces();
		pixelIdCount = 1;
		allPixelIdMap = new HashMap<>();
		pixelCoordMap = new HashMap<>();
		frontPixelsMap = new HashMap<>();
		leftPixelsMap = new HashMap<>();
		rightPixelsMap = new HashMap<>();
		upPixelsMap = new HashMap<>();
		downPixelsMap = new HashMap<>();
		createPixels();
		createPixelAdjacencyLists();
		calcPixelFormFactors();
	}
	
	private void createFaces() {
		// create center pts for all faces
		frontCtrPt = origin.add(zAxis.scalarMultiply(SIDELEN / 2));
		leftCtrPt = origin.add(zAxis.scalarMultiply(SIDELEN / 4));
		leftCtrPt = leftCtrPt.add(xAxis.scalarMultiply(SIDELEN / 2));
		rightCtrPt = origin.add(zAxis.scalarMultiply(SIDELEN / 4));
		rightCtrPt = rightCtrPt.add(xAxis.scalarMultiply(-1 * SIDELEN / 2));
		upCtrPt = origin.add(zAxis.scalarMultiply(SIDELEN / 4));
		upCtrPt = upCtrPt.add(yAxis.scalarMultiply(SIDELEN / 2));
		downCtrPt = origin.add(zAxis.scalarMultiply(SIDELEN / 4));
		downCtrPt = downCtrPt.add(zAxis.scalarMultiply(-1 * SIDELEN / 2));

		// generate corner pts for all faces
		frontFacePts = Utils.getPtsFromCenter(frontCtrPt, xAxis, yAxis, SIDELEN,
				SIDELEN);
		leftFacePts = Utils.getPtsFromCenter(leftCtrPt, yAxis, zAxis, SIDELEN,
				SIDELEN / 2);
		rightFacePts = Utils.getPtsFromCenter(rightCtrPt, zAxis, yAxis, SIDELEN / 2,
				SIDELEN);
		upFacePts = Utils.getPtsFromCenter(upCtrPt, zAxis, xAxis, SIDELEN / 2,
				SIDELEN);
		downFacePts = Utils.getPtsFromCenter(downCtrPt, xAxis, zAxis, SIDELEN,
				SIDELEN / 2);

		// generate face planes
		frontFace = new Plane(frontCtrPt, zAxis, TOLERANCE);
		leftFace = new Plane(leftCtrPt, xAxis, TOLERANCE);
		rightFace = new Plane(rightCtrPt, xAxis.negate(), TOLERANCE);
		upFace = new Plane(upCtrPt, yAxis, TOLERANCE);
		downFace = new Plane(downCtrPt, yAxis.negate(), TOLERANCE);
	}

	private void createPixels() {
		// generating pixel center pts for all faces
		HashSet<Vector3D> frontPixelCtrs = createPixelCtrs(frontCtrPt, xAxis, yAxis,
				SIDELEN, SIDELEN);
		HashSet<Vector3D> leftPixelCtrs = createPixelCtrs(leftCtrPt, yAxis, zAxis,
				SIDELEN, SIDELEN / 2);
		HashSet<Vector3D> rightPixelCtrs = createPixelCtrs(rightCtrPt, zAxis, yAxis,
				SIDELEN / 2, SIDELEN);
		HashSet<Vector3D> upPixelCtrs = createPixelCtrs(upCtrPt, zAxis, xAxis,
				SIDELEN / 2, SIDELEN);
		HashSet<Vector3D> downPixelCtrs = createPixelCtrs(downCtrPt, xAxis, zAxis,
				SIDELEN, SIDELEN / 2);

		// creating all pixels and adding them to the HashMap
		for (Vector3D pxCtr : frontPixelCtrs) {
			Pixel p = new Pixel(pxCtr, HemiFaces.FRONT, xAxis, yAxis, zAxis,
					PIXELLEN);
			allPixelIdMap.put(pixelIdCount, p);
			pixelCoordMap.put(p.getCenter(), pixelIdCount);
			int pixelLoc = classifyPixelLocation(p.getCenter(), frontCtrPt, xAxis,
					yAxis, SIDELEN, SIDELEN);
			if (frontPixelsMap.containsKey(pixelLoc)) {
				frontPixelsMap.get(pixelLoc).add(pixelIdCount);
			} else {
				ArrayList<Integer> al = new ArrayList<>();
				al.add(pixelIdCount);
				frontPixelsMap.put(pixelLoc, al);
			}
			pixelIdCount++;
		}
		
		for (Vector3D pxCtr : leftPixelCtrs) {
			Pixel p = new Pixel(pxCtr, HemiFaces.LEFT, xAxis, yAxis, zAxis,
					PIXELLEN);
			allPixelIdMap.put(pixelIdCount, p);
			pixelCoordMap.put(p.getCenter(), pixelIdCount);
			int pixelLoc = classifyPixelLocation(p.getCenter(), leftCtrPt, yAxis, 
					zAxis, SIDELEN, SIDELEN / 2);
			if (leftPixelsMap.containsKey(pixelLoc)) {
				leftPixelsMap.get(pixelLoc).add(pixelIdCount);
			} else {
				ArrayList<Integer> al = new ArrayList<>();
				al.add(pixelIdCount);
				leftPixelsMap.put(pixelLoc, al);
			}
			pixelIdCount++;
		}
		
		for (Vector3D pxCtr : rightPixelCtrs) {
			Pixel p = new Pixel(pxCtr, HemiFaces.RIGHT, xAxis, yAxis, zAxis,
					PIXELLEN);
			allPixelIdMap.put(pixelIdCount, p);
			pixelCoordMap.put(p.getCenter(), pixelIdCount);
			int pixelLoc = classifyPixelLocation(p.getCenter(), rightCtrPt, zAxis, 
					yAxis, SIDELEN / 2, SIDELEN);
			if (rightPixelsMap.containsKey(pixelLoc)) {
				rightPixelsMap.get(pixelLoc).add(pixelIdCount);
			} else {
				ArrayList<Integer> al = new ArrayList<>();
				al.add(pixelIdCount);
				rightPixelsMap.put(pixelLoc, al);
			}
			pixelIdCount++;
		}
		
		for (Vector3D pxCtr : upPixelCtrs) {
			Pixel p = new Pixel(pxCtr, HemiFaces.UP, xAxis, yAxis, zAxis,
					PIXELLEN);
			allPixelIdMap.put(pixelIdCount, p);
			pixelCoordMap.put(p.getCenter(), pixelIdCount);
			int pixelLoc = classifyPixelLocation(p.getCenter(), upCtrPt, zAxis, 
					xAxis, SIDELEN / 2, SIDELEN);
			if (upPixelsMap.containsKey(pixelLoc)) {
				upPixelsMap.get(pixelLoc).add(pixelIdCount);
			} else {
				ArrayList<Integer> al = new ArrayList<>();
				al.add(pixelIdCount);
				upPixelsMap.put(pixelLoc, al);
			}
			pixelIdCount++;
		}
		
		for (Vector3D pxCtr : downPixelCtrs) {
			Pixel p = new Pixel(pxCtr, HemiFaces.DOWN, xAxis, yAxis, zAxis,
					PIXELLEN);
			allPixelIdMap.put(pixelIdCount, p);
			pixelCoordMap.put(p.getCenter(), pixelIdCount);
			int pixelLoc = classifyPixelLocation(p.getCenter(), downCtrPt, xAxis, 
					zAxis, SIDELEN, SIDELEN / 2);
			if (downPixelsMap.containsKey(pixelLoc)) {
				downPixelsMap.get(pixelLoc).add(pixelIdCount);
			} else {
				ArrayList<Integer> al = new ArrayList<>();
				al.add(pixelIdCount);
				downPixelsMap.put(pixelLoc, al);
			}
			pixelIdCount++;
		}
	}

	private HashSet<Vector3D> createPixelCtrs(Vector3D ctrPt, Vector3D axis1,
			Vector3D axis2, double sideLen1, double sideLen2){
		// generate pixel center pts for one quadrant of the face
		HashSet<Vector3D> pixelCtrs = new HashSet<>();
		
		Vector3D p0 = ctrPt.add(axis1.scalarMultiply(PIXELLEN / 2));
		p0 = p0.add(axis2.scalarMultiply(PIXELLEN / 2));
		
		Vector3D pt = new Vector3D(1, p0);
		
		int k = 1;
		while (Utils.withinBounds(pt, ctrPt, axis1, axis2, sideLen1, sideLen2,
				TOLERANCE) == 0){
			while (Utils.withinBounds(pt, ctrPt, axis1, axis2, sideLen1, sideLen2,
					TOLERANCE) == 0){
				pixelCtrs.add(pt);
				pt = pt.add(axis2.scalarMultiply(PIXELLEN));
			}
			pt = p0;
			pt = pt.add(axis1.scalarMultiply(PIXELLEN * k));
			k++;
		}
		
		// copy pts to another quadrant
		HashSet<Vector3D> pixelCtrs2 = new HashSet<>();

		for (Vector3D pxCtr : pixelCtrs) {
			Vector3D newPxCtr = pxCtr.subtract(axis1.scalarMultiply(sideLen1 / 2));
			pixelCtrs2.add(newPxCtr);
		}

		pixelCtrs.addAll(pixelCtrs2);

		// pixelCtrs now contains pixel center pts covering half the face
		// copy pixel center pts to the other half of face
		HashSet<Vector3D> pixelCtrs3 = new HashSet<>();
		for (Vector3D pxCtr : pixelCtrs) {
			Vector3D newPxCtr = pxCtr.subtract(axis2.scalarMultiply(sideLen2 / 2));
			pixelCtrs3.add(newPxCtr);
		}

		pixelCtrs.addAll(pixelCtrs3);

		return pixelCtrs;
	}
	
	private int classifyPixelLocation(Vector3D pixelCtr, Vector3D faceCtr, 
			Vector3D axis1, Vector3D axis2, double sideLen1, double sideLen2) {
		
		Vector3D pxCtrLessAxis1 = pixelCtr.subtract(axis1.scalarMultiply(PIXELLEN));
		Vector3D pxCtrMoreAxis1 = pixelCtr.add(axis1.scalarMultiply(PIXELLEN));
		Vector3D pxCtrLessAxis2 = pixelCtr.subtract(axis2.scalarMultiply(PIXELLEN));
		Vector3D pxCtrMoreAxis2 = pixelCtr.add(axis2.scalarMultiply(PIXELLEN));
		
		boolean lessAxis1InBounds = Utils.withinBounds(pxCtrLessAxis1, faceCtr,
				axis1, axis2, sideLen1, sideLen2, TOLERANCE) == 0;
		boolean moreAxis1InBounds = Utils.withinBounds(pxCtrMoreAxis1, faceCtr,
				axis1, axis2, sideLen1, sideLen2, TOLERANCE) == 0;
		boolean lessAxis2InBounds = Utils.withinBounds(pxCtrLessAxis2, faceCtr,
				axis1, axis2, sideLen1, sideLen2, TOLERANCE) == 0;
		boolean moreAxis2InBounds = Utils.withinBounds(pxCtrMoreAxis2, faceCtr,
				axis1, axis2, sideLen1, sideLen2, TOLERANCE) == 0;
		
		if (!moreAxis1InBounds && !moreAxis2InBounds) {
			return 4; // pixel is at corner with highest axis 1 and 2 values
		} else if(!lessAxis1InBounds && !lessAxis2InBounds) {
			return -4; // pixel is at corner with lowest axis 1 and 2 values
		} else if(!moreAxis1InBounds && !lessAxis2InBounds) {
			return 3; // pixel is at corner with highest axis 1 value and lowest 2 value
		} else if(!lessAxis1InBounds && !moreAxis2InBounds) {
			return -3; // pixel is at corner with lowest axis 1 value and highest 2 value
		} else if(!moreAxis1InBounds) {
			return 2; // pixel is at an edge with highest axis 1 value
		} else if(!lessAxis1InBounds) {
			return -2; // pixel is at an edge with lowest axis 1 value
		} else if(!moreAxis2InBounds) {
			return 1; // pixel is at an edge with highest axis 2 value
		} else if(!lessAxis2InBounds) {
			return -1; // pixel is at an edge with lowest axis 2 value
		} else {
			return 0;
		}
	}

	private void createPixelAdjacencyLists() {
		// front face
		makeFacePxAdjList(frontPixelsMap.get(0), xAxis, yAxis);
		// find edge pixels adj lists
		// find corner pixels adj lists
		
		// left face
		makeFacePxAdjList(leftPixelsMap.get(0), yAxis, zAxis);
		
		// right face
		makeFacePxAdjList(rightPixelsMap.get(0), zAxis, yAxis);
		
		// up face
		makeFacePxAdjList(upPixelsMap.get(0), zAxis, xAxis);
		
		// down face
		makeFacePxAdjList(downPixelsMap.get(0), xAxis, zAxis);
	}

	private void makeFacePxAdjList(ArrayList<Integer> pixelIdList, Vector3D axis1,
			Vector3D axis2) {

		for (Integer pixelId : pixelIdList) {
			Pixel p = allPixelIdMap.get(pixelId);
			
			// find pixels adjacent to p
			// NOTE: locating pixels by coordinates may not be exact, to be tested
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					// skip p itself
					if (i == 0 && j == 0) {
						continue;
					}
					Vector3D newPxCtr = p.getCenter();
					newPxCtr = newPxCtr.add(axis1.scalarMultiply(i * PIXELLEN));
					newPxCtr = newPxCtr.add(axis2.scalarMultiply(j * PIXELLEN));
					Integer adjPx = pixelCoordMap.get(newPxCtr);
					
					if (adjPx != null) {
						p.getAdjacentPixels().add(adjPx);
					}
				}
			}
		}
	}

	private void makeEdgePxAdjList(ArrayList<Integer> pixelIdList, 
		ArrayList<Integer> adjSurfPxIdList, Vector3D axis1, Vector3D axis2,
		Vector3D edgeAxis) {
		
		// find adj pixels on the same face by calling makeFacePxAdjList()
		makeFacePxAdjList(pixelIdList, axis1, axis2);
		
		// find adj pixels on adj surface, along edge
		for (Integer pixelId : pixelIdList) {
			Pixel p = allPixelIdMap.get(pixelId);
			Vector3D pCtr = p.getCenter();
			Vector3D pCtrPlus = pCtr.add(edgeAxis.scalarMultiply(PIXELLEN));
			Vector3D pCtrMinus = pCtr.subtract(edgeAxis.scalarMultiply(PIXELLEN));
			
			for (Integer adjSurfPxId : adjSurfPxIdList) {
				Pixel q = allPixelIdMap.get(adjSurfPxId);
				Vector3D qCtr = q.getCenter();
				
				if (Utils.equalAlongAxis(qCtr, pCtr, edgeAxis, TOLERANCE) ||
						Utils.equalAlongAxis(qCtr, pCtrPlus, edgeAxis, TOLERANCE) ||
						Utils.equalAlongAxis(qCtr, pCtrMinus, edgeAxis, TOLERANCE)) {
					p.getAdjacentPixels().add(adjSurfPxId);
				}
			}
		}
	}

	// TODO
	private void makeCornerPxAdjList() {
		
	}

	// TODO
	private void calcPixelFormFactors() {
		
	}
}