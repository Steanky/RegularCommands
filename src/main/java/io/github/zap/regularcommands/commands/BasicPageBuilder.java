package io.github.zap.regularcommands.commands;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BasicPageBuilder implements PageBuilder {
    public static final String NAVBAR_KEY = "feedback.page.navbar";

    private static final int DEFAULT_ENTRIES_PER_PAGE = 8;

    private final int entriesPerPage;
    private int componentsOnLastPage = 0;
    private final List<Component[]> pages;

    public BasicPageBuilder(int entriesPerPage) {
        Validate.isTrue(entriesPerPage > 0, "entriesPerPage must be >= 0");
        this.entriesPerPage = entriesPerPage;
        this.pages = new ArrayList<>();
    }

    public BasicPageBuilder() {
        this(DEFAULT_ENTRIES_PER_PAGE);
    }

    @Override
    public void addEntry(@NotNull CommandForm<?> form) {
        Component[] components = pages.get(pages.size() - 1);
        if(componentsOnLastPage >= components.length) {
            pages.add(components = new Component[entriesPerPage]);
            componentsOnLastPage = 0;
        }

        components[componentsOnLastPage] = form.getUsage();
    }

    @Override
    public @NotNull Component getPage(int index) {
        if(index < pages.size()) {
            Component[] pageBody = pages.get(index);

            Component header = Component.translatable(NAVBAR_KEY, Component.text(index + 1),
                    Component.text(pages.size()));

            Component[] page = new Component[pageBody.length + 1];
            page[0] = header;

            System.arraycopy(pageBody, 0, page, 1, page.length - 1);
            return Component.join(Component.newline(), page);
        }

        throw new IndexOutOfBoundsException("index " + index + " out of bounds for length " + pages.size());
    }

    @Override
    public int pageCount() {
        return pages.size();
    }
}
