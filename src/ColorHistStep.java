import boofcv.abst.feature.detect.line.DetectLineSegment;
import boofcv.abst.segmentation.ImageSuperpixels;
import boofcv.alg.color.ColorHsv;
import boofcv.core.image.ConvertImage;
import boofcv.factory.feature.detect.line.ConfigLineRansac;
import boofcv.factory.feature.detect.line.FactoryDetectLine;
import boofcv.factory.segmentation.ConfigWatershed;
import boofcv.factory.segmentation.FactoryImageSegmentation;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import georegression.metric.UtilAngle;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

class ColorHistStep extends ProcessStep {

	BufferedImage i = null;
	float hue = 0f;
	float saturation = 0f;

	@Override
	protected Image process(BufferedImage in) {
		detectLineSegments(in);
		return in;
	}

	void detectLineSegments(BufferedImage image) {
		// convert the line into a single band image
		i=image;
		GrayU8 gray = ConvertBufferedImage.convertFromSingle(image, null, GrayU8.class);
		Planar<GrayF32> input = ConvertBufferedImage.convertFromPlanar(image, null, true, GrayF32.class);
		Planar<GrayF32> hsv = input.createSameShape();

		ColorHsv.rgbToHsv(input,hsv);

		float maxDist2 = 0.8f*0.8f;

		// Extract hue and saturation bands which are independent of intensity
		GrayF32 H = hsv.getBand(2);
		GrayF32 S = hsv.getBand(1);

		// Adjust the relative importance of Hue and Saturation.
		// Hue has a range of 0 to 2*PI and Saturation from 0 to 1.
		float adjustUnits = (float)(Math.PI/2.0);

		// step through each pixel and mark how close it is to the selected color
		BufferedImage output = new BufferedImage(input.width,input.height,BufferedImage.TYPE_INT_RGB);
		for( int y = 0; y < hsv.height; y++ ) {
			for( int x = 0; x < hsv.width; x++ ) {
				// Hue is an angle in radians, so simple subtraction doesn't work
				float dh = UtilAngle.dist(H.unsafe_get(x,y),hue);
				float ds = (S.unsafe_get(x,y)-saturation)*adjustUnits;

				// this distance measure is a bit naive, but good enough for to demonstrate the concept
				float dist2 = dh*dh + ds*ds;
				if( dist2 <= maxDist2 ) {
					output.setRGB(x,y,image.getRGB(x,y));
				}
			}
		}

		draw(image, output);
	}

	void draw(BufferedImage buffered, BufferedImage img) {
		Graphics g = buffered.createGraphics();
		g.drawImage(img,0,0,null);
	}

	@Override
	public void updateOutputDim(Dimension d) {
		this.outputDim = new Dimension(d);
	}

	@Override
	protected boolean handleClick(MouseEvent e) {
		float[] color = new float[3];
		if(i!=null)
		{
			int rgb = i.getRGB(e.getX(), e.getY());
			ColorHsv.rgbToHsv((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, color);
			hue = color[2];
			saturation = color[1];
			return true;
		}
		return false;
	}

}