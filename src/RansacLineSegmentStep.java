import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import boofcv.abst.feature.detect.line.DetectLineSegment;
import boofcv.factory.feature.detect.line.ConfigLineRansac;
import boofcv.factory.feature.detect.line.FactoryDetectLine;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import georegression.struct.line.LineSegment2D_F32;

class RansacLineSegmentStep extends ProcessStep {

	@Override
	protected Image process(BufferedImage in) {
		detectLineSegments(in);
		return in;
	}

	void detectLineSegments(BufferedImage image) {
		// convert the line into a single band image
		GrayU8 gray = ConvertBufferedImage.convertFromSingle(image, null, GrayU8.class);

		DetectLineSegment<GrayU8> detector = FactoryDetectLine.lineRansac(new ConfigLineRansac(100, 50, 70, true),
				GrayU8.class);

		draw(image, gray, detector, Color.RED);
	}

	void draw(BufferedImage buffered, GrayU8 gray, DetectLineSegment<GrayU8> detector, Color c) {
		Graphics g = buffered.createGraphics();
		g.setColor(c);
		List<LineSegment2D_F32> found = detector.detect(gray);
		for (LineSegment2D_F32 line : found) {
			int x1 = (int) line.a.x;
			int y1 = (int) line.a.y;
			int x2 = (int) line.b.x;
			int y2 = (int) line.b.y;
			g.drawLine(x1, y1, x2, y2);
		}
	}

	@Override
	public void updateOutputDim(Dimension d) {
		this.outputDim = new Dimension(d);
	}

	@Override
	protected boolean handleClick(MouseEvent e) {
		return false;
	}

}