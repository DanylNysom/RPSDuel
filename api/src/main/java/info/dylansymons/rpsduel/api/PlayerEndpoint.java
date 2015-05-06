package info.dylansymons.rpsduel.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "playerApi",
        version = "v1",
        resource = "player",
        namespace = @ApiNamespace(
                ownerDomain = "api.rpsduel.dylansymons.info",
                ownerName = "api.rpsduel.dylansymons.info",
                packagePath = ""
        )
)
public class PlayerEndpoint {

    private static final Logger logger = Logger.getLogger(PlayerEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Player.class);
    }

    /**
     * Returns the {@link Player} with the corresponding ID.
     *
     * @param email the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Player} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "player/{email}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Player get(@Named("email") String email) throws NotFoundException {
        logger.info("Getting Player with ID: " + email);
        Player player = ofy().load().type(Player.class).id(email).now();
        if (player == null) {
            throw new NotFoundException("Could not find Player with ID: " + email);
        }
        return player;
    }

    /**
     * Inserts a new {@code Player}.
     */
    @ApiMethod(
            name = "insert",
            path = "player",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Player insert(Player player) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that player.email has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(player).now();
        logger.info("Created Player.");

        return ofy().load().entity(player).now();
    }

    /**
     * Updates an existing {@code Player}.
     *
     * @param email  the ID of the entity to be updated
     * @param player the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code email} does not correspond to an existing
     *                           {@code Player}
     */
    @ApiMethod(
            name = "update",
            path = "player/{email}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Player update(@Named("email") String email, Player player) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(email);
        ofy().save().entity(player).now();
        logger.info("Updated Player: " + player);
        return ofy().load().entity(player).now();
    }

    /**
     * Deletes the specified {@code Player}.
     *
     * @param email the ID of the entity to delete
     * @throws NotFoundException if the {@code email} does not correspond to an existing
     *                           {@code Player}
     */
    @ApiMethod(
            name = "remove",
            path = "player/{email}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("email") String email) throws NotFoundException {
        checkExists(email);
        ofy().delete().type(Player.class).id(email).now();
        logger.info("Deleted Player with ID: " + email);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "player",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Player> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Player> query = ofy().load().type(Player.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Player> queryIterator = query.iterator();
        List<Player> playerList = new ArrayList<>(limit);
        while (queryIterator.hasNext()) {
            playerList.add(queryIterator.next());
        }
        return CollectionResponse.<Player>builder().setItems(playerList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(String email) throws NotFoundException {
        try {
            ofy().load().type(Player.class).id(email).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Player with ID: " + email);
        }
    }
}