package io.github.zap.regularcommands.commands;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BasicPageBuilder implements PageBuilder {
    private static final int DEFAULT_ENTRIES_PER_PAGE = 8;

    private final int entriesPerPage;
    private final List<Component[]> pages;
    private int lastPageSize = 0;


    public BasicPageBuilder(int entriesPerPage) {
        Validate.isTrue(entriesPerPage > 0, "entriesPerPage must be > 0");
        this.pages = new ArrayList<>();
        this.entriesPerPage = entriesPerPage;
    }

    public BasicPageBuilder() {
        this(DEFAULT_ENTRIES_PER_PAGE);
    }

    @Override
    public void addEntry(@NotNull CommandForm<?> form) {
        Component[] lastPageArray = pages.get(pages.size() - 1);
        if(lastPageSize >= lastPageArray.length) {
            pages.add(lastPageArray = new Component[entriesPerPage]);
            lastPageSize = 0;
        }

        lastPageArray[lastPageSize++] = form.getUsage();
    }

    @Override
    public @NotNull Component getPage(int index) {
        if(index < pages.size()) {
            Component[] pageBody = pages.get(index);
            Component[] fullPage = new Component[pageBody.length + 1];
            fullPage[0] = Component.translatable(DefaultKeys.NAVBAR.key(), Component.text(index + 1),
                    Component.text(pages.size()));

            System.arraycopy(pageBody, 0, fullPage, 1, pageBody.length);
            return Component.join(Component.newline(), Arrays.stream(fullPage).filter(Objects::nonNull).collect(Collectors.toList()));
        }

        throw new IndexOutOfBoundsException("index " + index + " out of bounds for length " + pages.size());
    }

    @Override
    public int pageCount() {
        return pages.size();
    }
}
