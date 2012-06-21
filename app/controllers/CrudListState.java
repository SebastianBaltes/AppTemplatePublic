package controllers;

public class CrudListState {
    
    public String crudBaseUrl; 
    public Integer rowsToShow;
    public String currentSortBy;
    public String currentOrder;
    public String currentFilter;
    
    public CrudListState(String crudBaseUrl, Integer rowsToShow, String currentSortBy, String currentOrder, String currentFilter) {
        this.crudBaseUrl = crudBaseUrl;
        this.rowsToShow = rowsToShow;
        this.currentSortBy = currentSortBy;
        this.currentOrder = currentOrder;
        this.currentFilter = currentFilter;
    }
    
}
