package elegit.treefx;

import elegit.Main;
import elegit.models.CommitHelper;
import elegit.treefx.CommitTreeController;
import elegit.treefx.TreeGraph;
import elegit.treefx.TreeLayout;
import io.reactivex.Observable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import org.apache.http.annotation.ThreadSafe;

/**
 * Class for the local and remote panel views that handles the drawing of a tree structure
 * from a given treeGraph.
 *
 */
@ThreadSafe
// but critically only because of all the asserts requiring this be done only in the FX thread. Without that, it
// isn't threadsafe. This has bindings, etc, lots of things that require it to be done in FX thread.
public class CommitTreePanelView extends Region{

    /**
     * Constructs a new view for the commit tree
     */
    public CommitTreePanelView(){
        super();
        Main.assertFxThread();
        this.setMinHeight(0);
        initCommitTreeScrollPanes(CommitTreeModel.getCommitTreeModel().getTreeGraph());
    }

    /**
     * Helper method to initialize the commit tree scroll panes
     * @param treeGraph TreeGraph
     */
    private void initCommitTreeScrollPanes(TreeGraph treeGraph) {
        Main.assertFxThread();
        ScrollPane sp = treeGraph.getScrollPane();
        sp.setOnMouseClicked(event -> CommitTreeController.handleMouseClicked());
        getChildren().clear();
        getChildren().add(anchorScrollPane(sp));
    }

    /**
     * Handles the layout and display of the treeGraph. Creates a thread
     * in which to execute the TreeLayoutTask, and a thread that waits
     * for the layout to finish and then updates the view
     * @param treeGraph the graph to be displayed
     */
    synchronized void displayTreeGraph(TreeGraph treeGraph, CommitHelper commitToFocusOnLoad){
        Main.assertFxThread();
        System.out.println("before focus");
        //initCommitTreeScrollPanes(treeGraph);
        System.out.println("after init");
        //TreeLayout.doTreeLayout(treeGraph).subscribe();
        TreeLayout.doTreeLayout(treeGraph)
                .concatWith(Observable.fromCallable(() -> {
                    CommitTreeController.focusCommitInGraph(commitToFocusOnLoad);
                    return true;
                }))
                .subscribe();
        //CommitTreeController.focusCommitInGraph(commitToFocusOnLoad);  // SLOW
        System.out.println("after focus");
    }

    /**
     * Displays an empty scroll pane
     */
    void displayEmptyView(){
        Main.assertFxThread();
        ScrollPane sp = new ScrollPane();
        this.getChildren().clear();
        this.getChildren().add(anchorScrollPane(sp));
    }

    /**
     * Anchors the width and height of the scroll pane to the width and height of
     * the view to ensure the scroll pane expands appropriately on resize
     * @param sp the scrollpane to anchor
     * @return the passed in scrollpane after being anchored
     */
    private ScrollPane anchorScrollPane(ScrollPane sp){
        Main.assertFxThread();
        sp.prefWidthProperty().bind(this.widthProperty());
        sp.prefHeightProperty().bind(this.heightProperty());
        return sp;
    }

}
