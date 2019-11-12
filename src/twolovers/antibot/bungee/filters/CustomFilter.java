package twolovers.antibot.bungee.filters;

import java.util.logging.Filter;

public interface CustomFilter extends Filter {
    Filter inject();
}
