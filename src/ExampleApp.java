class ExampleApp
{
    public static void main(String[] args)
	{
        TinkerApp app = new TinkerApp();
        ProcessStep step1 = new ExampleProcessStep(app.getInputDim());
        ProcessStep step2 = new ExampleProcessStep(step1.getOutputDim());
        app.addStep(step1);
        app.addStep(step2);
		app.run();
	}
}