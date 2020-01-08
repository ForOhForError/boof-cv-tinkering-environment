import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Dimension;

abstract class ProcessStep
{
    protected BufferedImage buf;
    protected Dimension outputDim;

    protected abstract Image process(BufferedImage in);

    public BufferedImage doProcess(BufferedImage input)
    {
        prepBuf();
        Image proc = process(input);
        buf.getGraphics().drawImage(proc, 0, 0, null);
        return buf;
    }

    private void prepBuf()
    {
        if(
            buf == null || 
            buf.getWidth() != outputDim.getWidth() || 
            buf.getHeight() != outputDim.getHeight()
        )
        {
            buf = new BufferedImage
            (
                (int)outputDim.getWidth(), 
                (int)outputDim.getHeight(), 
                BufferedImage.TYPE_INT_RGB
            );
        }
    }

    public Dimension getOutputDim()
    {
        return outputDim;
    }

    protected void updateOutputDim(Dimension inputDim)
    {
        outputDim.setSize(inputDim);
    }
}