package org.gradle.internal.buildoption;

import org.gradle.cli.CommandLineParser;
import org.gradle.cli.ParsedCommandLine;

import java.util.Map;

/**
 * A build option that takes a boolean value.
 * <p>
 * If a command line option is provided, this build option automatically creates a disabled option out-of-the-box e.g. {@code "--no-daemon"} for the provided option {@code "--daemon"}.
 *
 * @since 4.3
 */
public abstract class BooleanBuildOption<T> extends AbstractBuildOption<T, BooleanCommandLineOptionConfiguration> {

    public BooleanBuildOption(String gradleProperty) {
        super(gradleProperty);
    }

    public BooleanBuildOption(String gradleProperty, BooleanCommandLineOptionConfiguration... commandLineOptionConfigurations) {
        super(gradleProperty, commandLineOptionConfigurations);
    }

    @Override
    public void applyFromProperty(Map<String, String> properties, T settings) {
        String value = properties.get(gradleProperty);

        if (value != null) {
            applyTo(isTrue(value), settings, Origin.forGradleProperty(gradleProperty));
        }
    }

    @Override
    public void configure(CommandLineParser parser) {
        for (BooleanCommandLineOptionConfiguration config : commandLineOptionConfigurations) {
            configureCommandLineOption(parser, new String[] {config.getLongOption()}, config.getDescription(), config.isDeprecated(), config.isIncubating());
            String disabledOption = getDisabledCommandLineOption(config);
            configureCommandLineOption(parser, new String[] {disabledOption}, config.getDisabledDescription(), config.isDeprecated(), config.isIncubating());
            parser.allowOneOf(config.getLongOption(), disabledOption);
        }
    }

    @Override
    public void applyFromCommandLine(ParsedCommandLine options, T settings) {
        for (BooleanCommandLineOptionConfiguration config : commandLineOptionConfigurations) {
            if (options.hasOption(config.getLongOption())) {
                applyTo(true, settings, Origin.forCommandLine(config.getLongOption()));
            }

            if (options.hasOption(getDisabledCommandLineOption(config))) {
                applyTo(false, settings, Origin.forCommandLine(getDisabledCommandLineOption(config)));
            }
        }
    }

    private String getDisabledCommandLineOption(BooleanCommandLineOptionConfiguration config) {
        return "no-" + config.getLongOption();
    }

    public abstract void applyTo(boolean value, T settings, Origin origin);
}