package eu.balev.davicasa.processors;

/**
 * Enumerates the options which the tool takes.
 */
public enum CLOptionsEnum
{
	SOURCE_DIR("sourcedir", true, true, "The directory where the images to be processed are located"),
	DRY_RUN("dryrun", false, false, "Specifies if the tool should make a dry run. This means that no changes will be made."),
	CLEAN_SRC_DUPLICATES("cleansrcduplicates", false, false, "Makes the tool remove the duplicate image files in the source folder"),
	COPY_RENAME("copyrename", false, false, "Copies the files from the source folder into the target folder and renames the source files according to a date/time pattern"),
	TARGET_DIR("targetdir", true, false, "The directory where the result of the processing should be stored");
	
	private final String name, description;
	private final boolean hasArg, required; 
	
	private CLOptionsEnum(String name, boolean hasArg, boolean required, String description)
	{
		this.name = name;
		this.description = description;
		this.hasArg = hasArg;
		this.required = required;
	}
	
	/**
	 * Returns the name of the option, e.g. <code>sourcedir</code>
	 * 
	 * @return the name of the option
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Return the description of the option.
	 * 
	 * @return the description of the option
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Returns if the option should have an argument.
	 * 
	 * @return if the option should have an argument
	 */
	public boolean hasArg()
	{
		return hasArg;
	}
	
	/**
	 * Returns <code>true</code> if the option is required.
	 * 
	 * @return <code>true</code> if the option is required.
	 */
	public boolean isRequired()
	{
		return required;
	}
}
