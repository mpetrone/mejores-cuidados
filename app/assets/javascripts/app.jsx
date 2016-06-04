var ProductDetail = React.createClass({
  loadProducts: function() {
    $.ajax({
      url: this.props.url,
      dataType: 'json',
      cache: false,
      success: function(data) {
        this.setState({data: data.items});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  getInitialState: function() {
    return {data: []};
  },
  componentDidMount: function() {
    this.loadProducts();
  },
  render: function() {
    return (
      <div className="">
        <h1>Productos</h1>
        <Search data={this.state.data} />
        <Products data={this.state.data} />
      </div>
    );
  }
});

var Products = React.createClass({
  render: function() {
    var productNodes = this.props.data.map(function (product) {
      return (
        <Product key={product.id}
                 location={product.location}
                 category={product.category}
                 description={product.description}
                 unitQuatity={product.unitQuatity}
                 ean={product.ean}
                 price={product.price}/>
      );
    });

    return (
      <div className="well">
        {productNodes}
      </div>
    );
  }
});

var Search = React.createClass({
  render: function() {
    return (
        <div className="row">
          <form id="searchForm" class="form-inline" onSubmit={this.handleSubmit}>
            <input class="form-control" type="search" name="search" placeholder="Search" required/>
            <button type="submit" class="btn btn-default">Send invitation</button>
          </form>
        </div>
    );
  },
  handleSubmit: function(e) {
    e.preventDefault();

    var formData = $("#searchForm").serialize();

    var saveUrl = "http:/localhost:9000/search?q=" + formData.search;
    $.ajax({
      url: saveUrl,
      method: 'GET',
      dataType: 'json',
      success: function(data) {
        this.setState({data: data.items});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(saveUrl, status, err.toString());
      }.bind(this)
    });
  }
});

var Product = React.createClass({
  render: function() {
    return (
      <blockquote>
        <p>{this.props.category}</p>
        <strong>{this.props.description}</strong>
        <small>{this.props.price}</small>
      </blockquote>
    );
  }
});


React.render(<ProductDetail url="http://localhost:9000/products" />, document.getElementById('content'));
