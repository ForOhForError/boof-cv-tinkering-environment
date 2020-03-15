import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


class ContourBoxStep extends ProcessStep
{
    ArrayList<ContourBoundingBox> bounds = new ArrayList<ContourBoundingBox>();
    @Override
    protected Image process(BufferedImage in) {
        bounds.clear();
        bounds.addAll(ContourFinder.process(in));
        for(ContourBoundingBox b:bounds)
        {
            b.draw(in.getGraphics());
        }
        return in;
    }

    @Override
	protected boolean handleClick(java.awt.event.MouseEvent e) {
		return false;
	}
}