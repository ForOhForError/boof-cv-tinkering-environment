import boofcv.abst.feature.detect.line.DetectLineSegment;
import boofcv.abst.segmentation.ImageSuperpixels;
import boofcv.core.image.ConvertImage;
import boofcv.factory.feature.detect.line.ConfigLineRansac;
import boofcv.factory.feature.detect.line.FactoryDetectLine;
import boofcv.factory.segmentation.ConfigFh04;
import boofcv.factory.segmentation.ConfigSegmentMeanShift;
import boofcv.factory.segmentation.ConfigWatershed;
import boofcv.factory.segmentation.FactoryImageSegmentation;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.ConvertImageMisc;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import georegression.struct.line.LineSegment2D_F32;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

class SuperpixelStep extends ProcessStep {

	@Override
	protected Image process(BufferedImage in) {
		detectLineSegments(in);
		return in;
	}

	void detectLineSegments(BufferedImage image) {
		// convert the line into a single band image
		GrayU8 gray = ConvertBufferedImage.convertFromSingle(image, null, GrayU8.class);
		Planar<GrayU8> planar = ConvertBufferedImage.convertFromPlanar(image, null, false, GrayU8.class);

		DetectLineSegment<GrayU8> detector = FactoryDetectLine.lineRansac(new ConfigLineRansac(100, 50, 70, true),
				GrayU8.class);


		//ImageSuperpixels alg = FactoryImageSegmentation.meanShift(new ConfigSegmentMeanShift(), planar.imageType);
		ImageSuperpixels alg = FactoryImageSegmentation.watershed(new ConfigWatershed(), planar.imageType);
		GrayS32 pixelToSegment = new GrayS32(planar.width,planar.height);
		GrayU8 gray2 = new GrayU8(planar.width,planar.height);
		alg.segment(planar,pixelToSegment);
		ConvertImage.convert(pixelToSegment, gray2);

		draw(image, gray2);
	}

	void draw(BufferedImage buffered, GrayU8 gray) {
		Graphics g = buffered.createGraphics();
		g.drawImage(ConvertBufferedImage.convertTo(gray, null),0,0,null);
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