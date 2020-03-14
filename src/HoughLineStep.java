import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import boofcv.abst.feature.detect.line.DetectLine;
import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.factory.feature.detect.line.ConfigHoughGradient;
import boofcv.factory.feature.detect.line.FactoryDetectLine;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import georegression.struct.line.LineParametric2D_F32;
import georegression.struct.point.Point2D_F32;

class HoughLineStep extends ProcessStep {

	private static final int maxLines = 0;

    @Override
    protected Image process(BufferedImage in) {
        detectLines(in);
        return in;
    }
	
	void detectLines(BufferedImage buffered)
	{
		// convert the line into a single band image
		GrayU8 input = ConvertBufferedImage.convertFromSingle(buffered, null, GrayU8.class);
		GrayU8 blurred = input.createSameShape();

		// Blur smooths out gradient and improves results
		GBlurImageOps.gaussian(input,blurred,0,10,null);

		DetectLine<GrayU8> detector = FactoryDetectLine.houghLineFoot(
				new ConfigHoughGradient(5,5,5,20,maxLines),null, GrayU8.class);

		detectLines(buffered, input, detector, Color.GREEN);
	}

	Point2D_F32 collide(LineParametric2D_F32 l1, LineParametric2D_F32 l2, float low, float high)
	{
		float m1, b1, m2, b2;
		double a1, a2;
		m1 = l1.slope.y / l1.slope.x;
		b1 = l1.p.y - (l1.p.x * m1);
		a1 = Math.atan2(l1.slope.y, l1.slope.x);
		m2 = l2.slope.y / l2.slope.x;
		b2 = l2.p.y - (l2.p.x * m2);
		a2 = Math.atan2(l2.slope.y, l2.slope.x);

		double diff = Math.abs(a1-a2)%Math.PI;
		if(diff<low || diff>high)
		{
			return null;
		}
		
		if (m1 == m2)
		{
			return null;
		}

		float x = (b2 - b1) / (m1 - m2);
		float y = m1 * x + b1;
		return new Point2D_F32(x,y);
	}

	void detectLines(BufferedImage buffered, GrayU8 gray , DetectLine<GrayU8> detector, Color c) {
		Graphics g = buffered.createGraphics();
		g.setColor(c);
		List<LineParametric2D_F32> found = detector.detect(gray);
		/**
		for(LineParametric2D_F32 line : found)
		{
			int x = (int)line.p.x;
			int y = (int)line.p.y;
			int vx = 10*(int)line.slope.x;
			int vy = 10*(int)line.slope.y;
			g.drawLine(x-vx, y-vy, x+vx, y+vy);
		}
		*/
		for(LineParametric2D_F32 l1 : found)
		{
			for(LineParametric2D_F32 l2 : found)
			{
				if(l1 != l2){
					Point2D_F32 coll = collide(l1, l2, 1.4f, 1.9f);
					if(coll != null)
					{
						g.fillOval((int)coll.x-2, (int)coll.y-2, 5, 5);
					}
				}
			}
		}
	}

    @Override
    public void updateOutputDim(Dimension d)
    {
        this.outputDim = new Dimension(d);
	}
	
	@Override
	protected boolean handleClick(java.awt.event.MouseEvent e) {
		return false;
	}

}