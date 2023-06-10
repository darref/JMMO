public enum enumDirection
{
        down,top,left,right,topLeft,topRight,downLeft,downRight, noDirection;

        public String toString(enumDirection en)
        {
            String result = new String();
            switch (en)
            {
                case down ->
                {
                    result = "down";
                }
                case top ->
                {
                    result = "top";
                }
                case left ->
                {
                    result = "left";
                }
                case right ->
                {
                    result = "right";
                }
                case downLeft ->
                {
                    result = "downLeft";
                }
                case downRight ->
                {
                    result = "downRight";
                }
                case topLeft ->
                {
                    result = "topLeft";
                }
                case topRight ->
                {
                    result = "topRight";
                }
                default ->
                {
                    result = "noDirection";
                }
            }
            return  result;
        }
}
