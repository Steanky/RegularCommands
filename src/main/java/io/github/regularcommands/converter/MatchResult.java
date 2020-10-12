package io.github.regularcommands.converter;

import io.github.regularcommands.commands.CommandForm;
import org.apache.commons.lang3.tuple.ImmutableTriple;

public class MatchResult {
    private final CommandForm form;
    private final boolean hasPermission;
    private final boolean matches;
    private final ImmutableTriple<Boolean, Object[], String> conversionResult;

    private MatchResult(CommandForm form, boolean hasPermission, boolean matches, ImmutableTriple<Boolean, Object[], String> conversionResult) {
        this.form = form;
        this.hasPermission = hasPermission;
        this.matches = matches;
        this.conversionResult = conversionResult;
    }

    public static MatchResult of(CommandForm form, boolean hasPermission, boolean matches, ImmutableTriple<Boolean, Object[], String> conversionResult) {
        return new MatchResult(form, hasPermission, matches, conversionResult);
    }

    public CommandForm getForm() {
        return form;
    }

    public boolean matches() {
        return matches;
    }

    public ImmutableTriple<Boolean, Object[], String> getConversionResult() {
        return conversionResult;
    }

    public boolean hasPermission() {
        return hasPermission;
    }
}