package controllers;

public class CrudListState extends CrudDetailsState {
    
    public Integer rowsToShow;
    public String currentSortBy;
    public String currentOrder;
    public String currentFilter;
    
    public CrudListState(String crudBaseUrl, String crudEntityLabel, Integer rowsToShow, String currentSortBy, String currentOrder, String currentFilter) {
    	super(crudBaseUrl, crudEntityLabel);
        this.rowsToShow = rowsToShow;
        this.currentSortBy = currentSortBy;
        this.currentOrder = currentOrder;
        this.currentFilter = currentFilter;
    }
    
}
