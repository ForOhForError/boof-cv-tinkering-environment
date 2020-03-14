import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.GrayS16;
import boofcv.struct.image.GrayU8;
import georegression.struct.point.Point2D_I32;

class CannyStep extends ProcessStep {

    private static CannyEdge<GrayU8, GrayS16> canny = 
    FactoryEdgeDetectors.canny(2, true, true, GrayU8.class, GrayS16.class);

    @Override
    protected Image process(BufferedImage in) {
        drawContours(in);
        return in;
    }

    public void drawContours(BufferedImage in)
	{
        List<Contour> contours = getCannyContours(in);
		Graphics g = in.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, outputDim.width, outputDim.height);
		g.setColor(Color.WHITE);
		for(Contour con:contours)
		{
			int[] xpoint = new int[con.external.size()];
			int[] ypoint = new int[con.external.size()];
			int i=0;
			for(Point2D_I32 pt:con.external)
			{
				xpoint[i]=pt.x;
				ypoint[i]=pt.y;
				i++;
			}
			g.drawPolygon(xpoint,ypoint,xpoint.length);
		}
    }
    
    public static List<Contour> getCannyContours(BufferedImage image) {
		GrayU8 gray = ConvertBufferedImage.convertFrom(image, (GrayU8) null);
		GrayU8 edgeImage = gray.createSameShape();
		canny.process(gray, 0.1f, 0.3f, edgeImage);
		List<Contour> contours = BinaryImageOps.contour(edgeImage, ConnectRule.EIGHT, null);

		return contours;
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