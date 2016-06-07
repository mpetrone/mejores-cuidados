var ProductDetail = React.createClass({
  getInitialState: function() {
    return {data: []};
  },
  componentDidMount: function() {
    this.doSearch('');
  },
  doSearch: function(query) {
    var searchUrl = "/search?q=" + query;
    $.ajax({
      url: searchUrl,
      method: 'GET',
      dataType: 'json',
      success: function(data) {
        this.setState({data: data.items});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(saveUrl, status, err.toString());
      }
    });
  },
  render: function() {
    return (
      <div>
        <h1>Productos</h1>
        <Search doSearch={this.doSearch} />
        <ProductsTable data={this.state.data} />
      </div>
    );
  }
});

var ProductsTable = React.createClass({
  render: function() {
    var productNodes = this.props.data.map(function (product) {
      return (
        <Product key={product.id}
                 location={product.location}
                 category={product.category}
                 description={product.description}
                 unitQuatity={product.unitQuatity}
                 ean={product.ean}
                 price={product.price}
        />
      );
    });

    return (
      <div class="table-responsive">
        <table class="table table-striped">
          <theader>
            <tr>
              <th>Location</th>
              <th>Category</th>
              <th>Description</th>
              <th>Unit Quantity</th>
              <th>Price</th>
            </tr>
          </theader>
          <tbody>
            {productNodes}
          </tbody> 
        </table>
      </div>
    );
  }
});

var Search = React.createClass({
  render:function(){
    return <input type="search" class="form-control" ref="searchInput" placeholder="Search Name" onChange={this.doSearch}/>
  },
  doSearch: function(e) {
    e.preventDefault();
    var query=this.refs.searchInput.getDOMNode().value; // this is the search text
    this.props.doSearch(query);
  }
});

var Product = React.createClass({
  render: function() {
    return (
      <tr>
        <th>{this.props.location}</th>
        <th>{this.props.category}</th>
        <th>{this.props.description}</th>
        <th>{this.props.unitQuatity}</th>
        <th>{this.props.price}</th>
      </tr>
    );
  }
});

React.render(<ProductDetail/>, document.getElementById('content'));
