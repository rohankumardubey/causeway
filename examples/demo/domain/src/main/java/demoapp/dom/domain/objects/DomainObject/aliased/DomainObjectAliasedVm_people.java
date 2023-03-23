package demoapp.dom.domain.objects.DomainObject.aliased;

import demoapp.dom._infra.values.ValueHolderRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.causeway.applib.annotation.*;
import org.apache.causeway.applib.services.bookmark.Bookmark;
import org.apache.causeway.applib.services.bookmark.BookmarkService;

@Collection()
@CollectionLayout()
@RequiredArgsConstructor
public class DomainObjectAliasedVm_people {

    @SuppressWarnings("unused")
    private final DomainObjectAliasedVm mixee;

    @MemberSupport
    public List<? extends Customer> coll() {
        return addressEntities.all();
    }

    @Inject
    ValueHolderRepository<String, ? extends Customer> addressEntities;

    @Action(semantics = SemanticsOf.SAFE)
    public Customer lookup(final String bookmark) {
        return bookmarkService.lookup(Bookmark.parseElseFail(bookmark), Customer.class).orElseThrow(() -> new org.apache.causeway.applib.exceptions.RecoverableException("No customer exists for that bookmark"));
    }

    @Inject BookmarkService bookmarkService;

}
